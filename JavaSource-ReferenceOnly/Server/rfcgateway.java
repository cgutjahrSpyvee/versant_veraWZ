package com.nbcu.html5_vra.portalservices;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.sap.mw.jco.IFunctionTemplate;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.runtime.PortalRuntime;
import com.sapportals.portal.prt.service.jco.IJCOClientService;

/**
 * rfcgateway - a single, generic Portal component that can invoke ANY RFC /
 * BAPI function module in the backend SAP system and return its complete result
 * as a JSON document.
 *
 * It replaces the per-RFC pattern used by vendorsearch, inbox, objectactions,
 * etc. Instead of hand-coding every IMPORT/EXPORT/TABLE field, it uses JCo
 * metadata (JCO.Record reflection) to:
 *   1. bind IMPORT scalar fields from request parameters by field name, and
 *   2. serialise the ENTIRE result (EXPORT params, TABLES, CHANGING params,
 *      including nested structures/tables) to JSON automatically.
 *
 * ---------------------------------------------------------------------------
 * INVOCATION
 * ---------------------------------------------------------------------------
 *   GET/POST  ...?rfc=Z_SF_I504_DISPLAY_INBOX_W2W&I_SSO=jdoe
 *
 *   - "rfc"   (required) : the function module name (case-insensitive)
 *   - "alias" (optional) : JCo system alias, defaults to SAP_R3
 *   - "mode"  (optional) : "describe" returns the function module signature
 *                          (import/export/table fields, types, lengths) as JSON
 *                          WITHOUT executing it - use it to discover what to
 *                          pass. Any other value (or absent) executes the RFC.
 *   - "format" (optional): "odata2" wraps the result in the OData v2 flavour
 *                          - everything under "d", and every table rendered as
 *                          { "results": [...] } so a UI5 (JSON)Model list
 *                          binding can consume it. Absent = native envelope.
 *   - "entitySet" (opt.) : only with format=odata2. Names one result table to
 *                          hoist to the canonical collection shape
 *                          { "d": { "results": [...] } }, bindable at /d/results.
 *   - every other request parameter whose name matches an IMPORT field of the
 *     function module is bound to that field. Unknown parameters are ignored.
 *
 * NOTE: format=odata2 is OData-*flavoured* JSON, not a compliant OData service.
 * There is no $metadata, no key-based reads, and no $filter/$expand push-down;
 * a true UI5 ODataModel will not bind to it - use a JSONModel. For native OData,
 * expose the RFC from the ABAP backend via SAP Gateway (SEGW) instead.
 *
 * For function modules that require STRUCTURE or TABLE imports (the "write"
 * RFCs such as Z_SFI_I508_VRA_VENSAVE), post a JSON envelope instead - see
 * bindImportsFromJson() at the bottom for the documented extension point.
 *
 * ---------------------------------------------------------------------------
 * RESPONSE SHAPE
 * ---------------------------------------------------------------------------
 *   {
 *     "success": true,
 *     "rfc": "Z_SF_I504_DISPLAY_INBOX_W2W",
 *     "exporting": { ...scalar & structure export params... },
 *     "tables":    { "IT_INVITE_REQ": [ {..row..}, {..row..} ], ... }
 *   }
 * on error:
 *   { "success": false, "rfc": "...", "error": "message" }
 */
public class rfcgateway extends AbstractPortalComponent
{
    private static final String DEFAULT_ALIAS = "SAP_R3";

    /**
     * Optional safety allow-list. If non-empty, only these function modules may
     * be called through the gateway. Leave empty to allow any FM the connection
     * user is authorised for (SAP RFC authority checks still apply either way).
     */
    private static final Set<String> ALLOWED_RFCS = new HashSet<String>();
    static {
        // ALLOWED_RFCS.add("Z_SF_I504_DISPLAY_INBOX_W2W");
        // ALLOWED_RFCS.add("Z_SF_I513_SEARCH_VENDOR_W2W");
    }

    public void doContent(IPortalComponentRequest request, IPortalComponentResponse res)
    {
        HttpServletResponse resp = request.getServletResponse(true);
        resp.setContentType("application/json; charset=UTF-8");

        PrintWriter out;
        try {
            out = resp.getWriter();
        } catch (Exception e) {
            res.write("{\"success\":false,\"error\":\"cannot obtain writer\"}");
            return;
        }

        String rfcName = request.getParameter("rfc");
        JCO.Client client = null;

        try {
            if (rfcName == null || rfcName.trim().length() == 0) {
                throw new Exception("Missing required parameter 'rfc'");
            }
            rfcName = rfcName.trim().toUpperCase();

            if (!ALLOWED_RFCS.isEmpty() && !ALLOWED_RFCS.contains(rfcName)) {
                throw new Exception("Function module not permitted: " + rfcName);
            }

            String alias = request.getParameter("alias");
            if (alias == null || alias.trim().length() == 0) {
                alias = DEFAULT_ALIAS;
            }

            // --- connect ------------------------------------------------------
            IJCOClientService clientService =
                (IJCOClientService) PortalRuntime.getRuntimeResources()
                    .getService(IJCOClientService.KEY);
            client = clientService.getJCOClient(alias, request);
            client.connect();

            IRepository repository = JCO.createRepository("repository", client);
            IFunctionTemplate template = repository.getFunctionTemplate(rfcName);
            if (template == null) {
                throw new Exception("Function module not found: " + rfcName);
            }

            // --- describe mode: publish the signature, do NOT execute ---------
            String mode = request.getParameter("mode");
            if (mode != null && mode.equalsIgnoreCase("describe")) {
                out.write(describe(rfcName, template));
                return;
            }

            JCO.Function function = new JCO.Function(template);

            // --- bind imports -------------------------------------------------
            bindImportsFromParams(function.getImportParameterList(), request);
            // For structure/table imports, see bindImportsFromJson(request, function).

            // --- execute ------------------------------------------------------
            client.execute(function);

            // --- serialise the full result -----------------------------------
            boolean odata2 = "odata2".equalsIgnoreCase(request.getParameter("format"));
            StringBuilder sb = new StringBuilder(4096);

            if (odata2) {
                // OData v2 flavour: everything under "d"; tables become { "results": [...] }.
                sb.append("{\"d\":");

                // Optional: hoist a single table to the canonical v2 collection
                // shape  { "d": { "results": [...] } }  so a list binding can use
                // the path /d/results directly.
                String entitySet = request.getParameter("entitySet");
                JCO.Table hoist = (entitySet == null) ? null : findTable(function, entitySet.toUpperCase());
                if (hoist != null) {
                    writeTable(sb, hoist, true);
                } else {
                    sb.append("{\"exporting\":");
                    writeRecord(sb, function.getExportParameterList(), true);

                    JCO.ParameterList tbl = function.getTableParameterList();
                    if (tbl != null && tbl.getFieldCount() > 0) {
                        sb.append(",\"tables\":");
                        writeRecord(sb, tbl, true);
                    }

                    JCO.ParameterList chg = function.getChangingParameterList();
                    if (chg != null && chg.getFieldCount() > 0) {
                        sb.append(",\"changing\":");
                        writeRecord(sb, chg, true);
                    }
                    sb.append("}");
                }
                sb.append("}");
            } else {
                // Native gateway envelope.
                sb.append("{\"success\":true,\"rfc\":");
                appendString(sb, rfcName);

                sb.append(",\"exporting\":");
                writeRecord(sb, function.getExportParameterList(), false);

                JCO.ParameterList tables = function.getTableParameterList();
                if (tables != null && tables.getFieldCount() > 0) {
                    sb.append(",\"tables\":");
                    writeRecord(sb, tables, false);
                }

                JCO.ParameterList changing = function.getChangingParameterList();
                if (changing != null && changing.getFieldCount() > 0) {
                    sb.append(",\"changing\":");
                    writeRecord(sb, changing, false);
                }
                sb.append("}");
            }
            out.write(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            StringBuilder err = new StringBuilder();
            err.append("{\"success\":false,\"rfc\":");
            appendString(err, rfcName == null ? "" : rfcName);
            err.append(",\"error\":");
            appendString(err, ex.getMessage() == null ? ex.toString() : ex.getMessage());
            err.append("}");
            out.write(err.toString());
        } finally {
            if (client != null) {
                try { client.disconnect(); } catch (Exception ignore) { }
            }
        }
    }

    // ======================================================================
    //  IMPORT binding
    // ======================================================================

    /**
     * For every scalar IMPORT field of the function module, copy the matching
     * request parameter (by field name) into the import list. Structure and
     * table imports are skipped here (handled by the JSON extension point).
     */
    private void bindImportsFromParams(JCO.ParameterList imports,
                                       IPortalComponentRequest request)
    {
        if (imports == null) return;
        int count = imports.getFieldCount();
        for (int i = 0; i < count; i++) {
            if (imports.isStructure(i) || imports.isTable(i)) {
                continue; // complex imports come via JSON envelope, not params
            }
            String fieldName = imports.getName(i);
            String value = request.getParameter(fieldName);
            if (value != null) {
                imports.setValue(value, fieldName); // JCo converts to field type
            }
        }
    }

    /**
     * Locate an executed table parameter by name. RFC result tables may sit in
     * either the EXPORT list (e.g. IT_VENDOR_SRCH, IT_INVITE_REQ) or the TABLES
     * list, so both are searched. Returns null if not found or not a table.
     */
    private JCO.Table findTable(JCO.Function function, String name)
    {
        JCO.ParameterList[] lists = new JCO.ParameterList[] {
            function.getExportParameterList(),
            function.getTableParameterList()
        };
        for (int l = 0; l < lists.length; l++) {
            JCO.ParameterList pl = lists[l];
            if (pl == null) continue;
            int count = pl.getFieldCount();
            for (int i = 0; i < count; i++) {
                if (pl.isTable(i) && name.equals(pl.getName(i))) {
                    return pl.getTable(i);
                }
            }
        }
        return null;
    }

    // ======================================================================
    //  DESCRIBE - publish the function module signature (no execution)
    // ======================================================================

    /**
     * Build a JSON description of the function module's signature by reading the
     * template's JCO.MetaData for each parameter list. Nothing is executed, so
     * this is safe to call for discovery/tooling.
     */
    private String describe(String rfcName, IFunctionTemplate template)
    {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("{\"success\":true,\"mode\":\"describe\",\"rfc\":");
        appendString(sb, rfcName);

        sb.append(",\"importing\":");
        writeParamMeta(sb, template.getImportParameterList());
        sb.append(",\"exporting\":");
        writeParamMeta(sb, template.getExportParameterList());
        sb.append(",\"tables\":");
        writeParamMeta(sb, template.getTableParameterList());
        sb.append(",\"changing\":");
        writeParamMeta(sb, template.getChangingParameterList());

        sb.append("}");
        return sb.toString();
    }

    /** Serialise one parameter list's metadata as a JSON array of field descriptors. */
    private void writeParamMeta(StringBuilder sb, JCO.MetaData meta)
    {
        sb.append("[");
        if (meta != null) {
            int count = meta.getFieldCount();
            for (int i = 0; i < count; i++) {
                if (i > 0) sb.append(",");
                writeFieldMeta(sb, meta, i);
            }
        }
        sb.append("]");
    }

    /** One field descriptor: name, type, length, optional flag, and nested fields for structures/tables. */
    private void writeFieldMeta(StringBuilder sb, JCO.MetaData meta, int i)
    {
        sb.append("{\"name\":");
        appendString(sb, meta.getName(i));
        sb.append(",\"type\":");
        appendString(sb, meta.getTypeAsString(i));
        sb.append(",\"length\":").append(meta.getLength(i));
        sb.append(",\"optional\":").append(meta.isOptional(i) ? "true" : "false");

        String description = meta.getDescription(i);
        if (description != null && description.length() > 0) {
            sb.append(",\"text\":");
            appendString(sb, description);
        }

        // Structures and tables carry their own row metadata - publish it too.
        if (meta.isStructure(i) || meta.isTable(i)) {
            JCO.MetaData rowMeta = meta.getRecordMetaData(i);
            sb.append(",\"fields\":");
            writeParamMeta(sb, rowMeta);
        }
        sb.append("}");
    }

    // ======================================================================
    //  Generic JSON serialisation of any JCO.Record (params / structure / row)
    // ======================================================================

    /** Serialise a Record (parameter list, structure, or a table's current row) as a JSON object. */
    private void writeRecord(StringBuilder sb, JCO.Record rec, boolean odata2)
    {
        sb.append("{");
        int count = rec.getFieldCount();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(",");
            appendString(sb, rec.getName(i));
            sb.append(":");
            writeField(sb, rec, i, odata2);
        }
        sb.append("}");
    }

    /** Serialise a single field, recursing into nested structures and tables. */
    private void writeField(StringBuilder sb, JCO.Record rec, int i, boolean odata2)
    {
        if (rec.isStructure(i)) {
            JCO.Structure struct = rec.getStructure(i);
            if (struct == null) { sb.append("null"); return; }
            writeRecord(sb, struct, odata2);
            return;
        }
        if (rec.isTable(i)) {
            writeTable(sb, rec.getTable(i), odata2);
            return;
        }

        // scalar
        String value = rec.getString(i);
        if (value == null) { sb.append("null"); return; }

        if (isJsonNumber(rec.getType(i), value)) {
            sb.append(value.trim());
        } else {
            appendString(sb, value);
        }
    }

    /**
     * Serialise a JCO.Table as a JSON array of row objects. When odata2 is set,
     * the array is wrapped in the OData v2 collection envelope { "results": [...] }
     * so a UI5 (JSON)Model list binding can point straight at it.
     */
    private void writeTable(StringBuilder sb, JCO.Table table, boolean odata2)
    {
        if (odata2) sb.append("{\"results\":");
        sb.append("[");
        if (table != null) {
            int rows = table.getNumRows();
            for (int r = 0; r < rows; r++) {
                table.setRow(r);
                if (r > 0) sb.append(",");
                writeRecord(sb, table, odata2); // Table exposes the current row as a Record
            }
        }
        sb.append("]");
        if (odata2) sb.append("}");
    }

    /**
     * Decide whether a scalar field should be emitted as a bare JSON number.
     * NUM fields are kept as strings on purpose to preserve SAP leading zeros
     * (e.g. vendor "0000012345"); DATE/TIME stay strings (YYYYMMDD / HHMMSS).
     */
    private boolean isJsonNumber(int jcoType, String value)
    {
        switch (jcoType) {
            case JCO.TYPE_INT:
            case JCO.TYPE_INT1:
            case JCO.TYPE_INT2:
            case JCO.TYPE_FLOAT:
            case JCO.TYPE_BCD:
                // guard against blank / non-numeric BCD strings from JCo
                if (value.trim().length() == 0) return false;
                for (int k = 0; k < value.length(); k++) {
                    char c = value.charAt(k);
                    if (!(Character.isDigit(c) || c == '-' || c == '+'
                          || c == '.' || c == 'E' || c == 'e')) {
                        return false;
                    }
                }
                return true;
            default:
                return false; // CHAR, NUM, DATE, TIME, STRING, byte, ...
        }
    }

    // ======================================================================
    //  JSON string escaping (replaces the fragile char-by-char replace() hacks)
    // ======================================================================

    /** Append a value as a correctly escaped, quoted JSON string. */
    private void appendString(StringBuilder sb, String s)
    {
        sb.append('"');
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append("\\u");
                        String hex = Integer.toHexString(c);
                        for (int p = hex.length(); p < 4; p++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ======================================================================
    //  Extension point: structure / table IMPORTS via a JSON request body
    // ======================================================================
    //
    // The param-based binder above covers every "read" RFC (search, inbox,
    // display...). For "write" RFCs that need structure/table imports, post a
    // JSON body such as:
    //
    //   {
    //     "rfc": "Z_SFI_I508_VRA_VENSAVE",
    //     "importing": { "E_SSO": "jdoe" },
    //     "structures": { "ES_REQ": { "REQTY": "N", "STATS": "S" } },
    //     "tables": {
    //       "ET_LFA1": [ { "NAME1": "ACME", "LAND1": "US" } ]
    //     }
    //   }
    //
    // and implement bindImportsFromJson(...) using whatever JSON parser is on
    // the portal classpath (org.json, Gson, Jackson). The read side stays
    // reflection-driven and needs no per-RFC code.
    //
    // private String readBody(IPortalComponentRequest request) throws Exception {
    //     BufferedReader r = request.getServletRequest().getReader();
    //     StringBuilder b = new StringBuilder();
    //     String line;
    //     while ((line = r.readLine()) != null) b.append(line);
    //     return b.toString();
    // }
}
