# Technical Architecture Document

## Technologies
This application utilizes the Android Compose development library and is written in Kotlin.

## Data model
### CDM (fr: MCD)
Voir le fichier `data_model.puml` qui contient le MCD du système.

### PDM (fr: MPD)

## External interfaces
| ID | interface name | type | business usage | main constraints |
|---|---|---|---|---|
| IF-EXT-001 | OpenLibrary (or equivalent ISBN lookup API) | public HTTP REST | retrieve book metadata from ISBN scan | must support local manual fallback if lookup fails (offline case) |
| IF-EXT-002 | ShareMyBook Lightweight Transaction Backend | HTTP REST | orchestrate loan workflow: init, accept, result using shareId | only 3 endpoints, minimal JSON payload, no authentication layer |
| IF-EXT-003 | CameraX Scanner (QR + Barcodes) | Android SDK / MLKit | decode QR codes (shareId) and ISBN barcodes | one single unified scan activity handles both types |
| IF-EXT-004 | Room / SQLite local DB | Android internal persistence | store books / users / loans | local DB is the primary source of truth, backend does not store book domain |
| IF-EXT-005 | Android Network Permission Layer | OS Android capability | establish network calls for APIs | offline-first required, internet only needed for transactions and lookups |

