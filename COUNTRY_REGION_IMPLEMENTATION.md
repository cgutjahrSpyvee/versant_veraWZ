# Country and Region Implementation Guide

## Overview
This document describes the implementation of country and state/region dropdown fields in the VeRA Fiori application. The implementation uses local JSON data instead of web service calls for better performance and offline capability.

## Implementation Details

### 1. Data File
**Location:** `webapp/model/countryRegionData.json`

The data file contains an array of objects with the following structure:
```json
{
  "country": "US",
  "region": "CA",
  "description": "California"
}
```

**Current Status:** The file currently contains sample data for:
- United States (US) - All 50 states + territories
- Andorra (AD) - 8 regions
- United Arab Emirates (AE) - 9 emirates

**To Add More Countries:** Simply append additional country/region entries to the JSON array following the same structure.

### 2. Service Layer Updates
**File:** `webapp/model/VeRAService.js`

#### Modified Methods:

**`getCountries()`**
- Loads data from local JSON file
- Extracts unique countries
- Returns sorted array of country objects with `country` and `description` properties

**`getRegions(sCountry)`**
- Loads data from local JSON file
- Optionally filters by country parameter
- Returns array of region objects

**`getAllRegionData()`** (New)
- Loads complete region data for client-side filtering
- Used by the Basic controller to enable dynamic region filtering

### 3. Controller Updates
**File:** `webapp/controller/steps/Basic.controller.js`

#### Key Changes:

**`onInit()`**
- Loads countries list
- Loads all region data into `/ui/allRegions`
- Initializes region filtering based on current country selection

**`onCountryChange(oEvent)`** (New)
- Triggered when user selects a country
- Clears the current state/region selection
- Filters regions based on selected country
- Validates the form

**`_filterRegionsByCountry()`** (New)
- Filters the complete region list based on selected country
- Updates `/ui/regions` with filtered results
- Handles empty country selection

### 4. View Updates
**File:** `webapp/view/steps/Basic.view.xml`

#### Modified Control:
The country Select control now uses the `onCountryChange` event handler instead of `onBasicFieldChange`:

```xml
<Select id="country" selectedKey="{reg>/basic/primaryAddress/country}"
        items="{reg>/ui/countries}" change=".onCountryChange">
    <core:ListItem key="{reg>country}" text="{reg>description}"/>
</Select>
```

## Data Model Structure

### Registration Model Paths:
- `/ui/countries` - Array of available countries
- `/ui/allRegions` - Complete array of all regions (for filtering)
- `/ui/regions` - Filtered array of regions for selected country
- `/basic/primaryAddress/country` - Selected country code
- `/basic/primaryAddress/state` - Selected region/state code

## User Experience Flow

1. User opens the Basic Information step
2. Countries dropdown is populated with all available countries
3. User selects a country from the dropdown
4. `onCountryChange` handler is triggered:
   - Current state/region selection is cleared
   - Regions are filtered to show only those for the selected country
   - State/region dropdown is updated with filtered options
5. User can now select from the filtered list of states/regions
6. Form validation ensures both country and state/region are selected

## Benefits

1. **Performance:** No web service calls needed for country/region data
2. **Offline Capability:** Works without backend connectivity
3. **User Experience:** Instant filtering of regions when country changes
4. **Maintainability:** Easy to add new countries/regions by updating JSON file
5. **Consistency:** Same data structure used throughout the application

## Adding New Countries/Regions

To add new countries and their regions:

1. Open `webapp/model/countryRegionData.json`
2. Add entries following this format:
```json
{
  "country": "CA",
  "region": "ON",
  "description": "Ontario"
}
```
3. Save the file
4. No code changes required - the implementation automatically handles new data

## Testing Checklist

- [ ] Country dropdown populates on page load
- [ ] Selecting a country clears the state/region field
- [ ] State/region dropdown shows only regions for selected country
- [ ] Changing country updates available regions
- [ ] Form validation works correctly
- [ ] Data persists when navigating between wizard steps
- [ ] Secondary addresses work correctly (if applicable)

## Future Enhancements

Potential improvements for future iterations:

1. Add country names (full descriptions) to the data file
2. Implement search/filter capability in dropdowns for large lists
3. Add support for postal code validation by country
4. Cache the JSON data to avoid repeated file loads
5. Add support for country-specific address formats
6. Implement lazy loading for large country/region datasets

## Notes

- The implementation maintains backward compatibility with existing code
- The original web service methods are replaced but maintain the same interface
- The data file can be easily replaced with a web service call if needed in the future
- All region filtering happens client-side for optimal performance
