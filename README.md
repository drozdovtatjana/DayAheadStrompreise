# Day-Ahead Electricity Price Tool (Austria)

A Java-based desktop application that retrieves Day-Ahead electricity prices for Austria from the aWATTar API, 
normalizes the data across three days, and visualizes hourly prices with explicit handling of missing data and daylight 
saving time transitions.

---
## How to Run the Program

1. Ensure **Java 11 or newer** is installed.
2. Run the project via IDE or command line from Main.java, Main.java is located in src/ui Repository
3. The application starts with a **date picker UI**.
4. After selecting a date:

* Day-Ahead prices for the selected day, the previous day, and the next day are fetched
* Normalized price data is printed to the console (JSON)
* An hourly price chart is displayed

---

## Programming Flow

### Project Planning

The project started with defining the core objectives:

* Fetch Day-Ahead electricity prices from an external API
* Normalize time-series data across multiple days
* Visualize hourly prices
* Provide normalized data output in JSON format

### API Integration

An API client was implemented to communicate with the aWATTar Marketdata API. DTOs were introduced to efficiently parse
and represent the raw API response.

### Data Parsing

The raw API data is converted into structured domain objects to decouple business logic from API-specific formats.

### GUI Design and Implementation

Different Java GUI options were evaluated. Swing was chosen for its simplicity and compatibility. A responsive UI was
implemented to display charts and allow date selection.

### Data Normalization

A normalization algorithm was introduced to:

* Generate a complete hourly grid
* Handle missing hours explicitly
* Convert all prices into a uniform unit

### Calendar Integration

A JDatePicker component was integrated to allow users to freely select the target date. The selected date drives the
entire data-fetching and normalization workflow.

### JSON Export

Normalized price data is exported as a JSON string and printed to the console, enabling inspection, logging, or reuse
in other tools.

### Refactoring and Architecture Design

After implementing core features, the codebase was refactored into a clean, layered architecture to improve 
maintainability, readability, and extensibility.

### Documentation

Comprehensive documentation was created to explain architecture, data flow, design decisions, and known limitations.

---


## Programming Language

The project is implemented in **Java**.

Java was chosen for:

* Type safety and compile-time error detection
* Robust standard libraries (`HttpClient`, `java.time`)
* Cross-platform compatibility
* Maintainability and scalability

Python was considered, but Java provides stronger structure and faster.

---

## Architectural Style

The project follows a **layered, hexagonal-inspired architecture** with clear separation of concerns:

### UI Layer (`ui`)

Swing-based date selection and chart visualization using XChart.

### Application Layer (`application`)

`PriceService` orchestrates the full workflow: data retrieval, mapping, and normalization.

### Infrastructure Layer (`infrastructure.api`, `infrastructure.json`)

Handles external API communication and JSON parsing/mapping.

### Domain Layer (`domain`)

Pure domain model (`PricePoint`), independent of UI, API, and serialization concerns.

### Utility Layer (`util`)

Shared logic such as time range generation and data normalization.

This architecture supports extensibility and clean separation between technical and business concerns.

---

## Design Patterns

* **Application Service / Facade** – `PriceService`
* **Adapter (DTO → Domain)** – `GsonPriceMapper`
* **Factory** – `TimeRangeFactory`
* **DTO Pattern** – `AwattarPriceDto`, `AwattarResponseDto`
* **Exporter Utility** – `JsonExporter`

---

## OOP Usage

* Single Responsibility Principle applied across classes
* Constructor-based dependency injection
* Extensible design suitable for future interfaces or exporters

---

## Robust Data Handling

* API responses are validated (non-200 responses fail explicitly)
* Missing hours are explicitly represented using `NaN`
* Prices are consistently converted from `EUR/MWh` to `ct/kWh`

---

## Data Normalization

Normalization is implemented in `PriceNormalizer`:

* A complete hourly grid is generated for:

  * the previous day
  * the selected day
  * the following day
* Uses `ZonedDateTime` with `Europe/Vienna`
* All hours are initialized as `NaN`
* Available prices overwrite the corresponding hour slot

This guarantees:
 
* Explicit representation of gaps
* Chronological ordering
* Consistent output for visualization and export

---

## Daylight Saving Time (Sommer-/Winterzeit)

DST is handled using:

* `ZoneId.of("Europe/Vienna")`
* `ZonedDateTime` for all time calculations
* Hour-by-hour iteration using `plusHours(1)`

Behavior:

* **23-hour days (DST start)** – missing hour is skipped
* **25-hour days (DST end)** – repeated local hour occurs twice with different UTC offsets

Both cases are preserved internally and exported with full offset information.

---

## Visualization of Missing Data

* A fixed hourly grid is always generated
* Missing values remain `NaN`
* Charts display:

  * continuous data as connected lines
  * missing data as visible gaps

This makes data quality issues explicit.

---

## Libraries Used

* **Gson** – JSON parsing and serialization
* **Java HttpClient** – HTTP communication
* **XChart** – time-series visualization
* **JDatePicker** – date selection
* **java.time API** – time zone and DST handling

No heavy frameworks (e.g. Spring) were used.

---

## Why Docker Was Not Used

Docker was omitted because:

* The application is a desktop Swing application
* No external runtime dependencies exist
* A standard Java runtime is sufficient

Docker would be more appropriate for backend or multi-service deployments.

---

## Testing

Testing focused on validating **time handling and normalization**:

* Manual verification of:

  * regular days (24h)
  * DST start (23h)
  * DST end (25h)
  * incomplete API data
* Console output and charts were used to visually confirm:

  * correct hour counts
  * explicit gaps
  * absence of duplicated or missing hours

Automated tests were planned as future work, especially for DST edge cases.

---

## Known Limitations / Future Improvements

* Limited HTTP error differentiation
* UI responsiveness could be improved using `SwingWorker`
* API locale (`at`) is currently hardcoded
* No automated tests yet
* Chart scalability for larger time ranges

## Bugs to Fix– Summer Time Chart Offset

* During summer time (CEST, UTC+2), the chart visualization starts one hour too early.
  * The hourly chart should start at 00:00 local time.
  * Currently, during the entire summer time period, the chart visually starts at 23:00.

* This issue affects only the visual representation:
  * Data fetching, normalization, and DST handling are correct.
  * All timestamps and exported data remain accurate.
  * No hours are lost or duplicated.

The issue is caused by time zone conversion when rendering the X-axis using Date objects and will be addressed in future improvements.


 


