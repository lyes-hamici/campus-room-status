# Architecture

## Code structure (concise)

Base path:
`room-status/room-status/src/main/java/com/example/room_status`

```
.
|-- config
|   |-- AppProperties.java
|   |-- CacheConfig.java
|   |-- GoogleClientConfig.java
|   |-- OpenApiConfig.java
|   \-- WebConfig.java
|
|-- controller
|   |-- BuildingController.java
|   |-- HealthController.java
|   \-- RoomController.java
|
|-- dto
|   |-- common
|   |   |-- BuildingDto.java
|   |   |-- EventDto.java
|   |   |-- FilterDto.java
|   |   |-- RoomDto.java
|   |   \-- RoomSummaryDto.java
|   |-- request
|   |   \-- RoomSearchRequest.java
|   \-- response
|       |-- ApiErrorResponse.java
|       |-- BuildingListResponse.java
|       |-- HealthResponse.java
|       |-- RoomDetailResponse.java
|       |-- RoomListResponse.java
|       \-- RoomScheduleResponse.java
|
|-- exception
|   |-- ErrorCode.java
|   |-- GlobalExceptionHandler.java
|   |-- GoogleIntegrationException.java
|   |-- InvalidRequestException.java
|   \-- RoomNotFoundException.java
|
|-- integration
|   \-- google
|       |-- GoogleAdminDirectoryClient.java
|       |-- GoogleCalendarClient.java
|       |-- GoogleConnectionChecker.java
|       \-- GoogleMapper.java
|
|-- mapper
|   |-- BuildingMapper.java
|   |-- EventMapper.java
|   \-- RoomMapper.java
|
|-- model
|   |-- Building.java
|   |-- Room.java
|   |-- RoomEvent.java
|   |-- RoomStatus.java
|   \-- RoomType.java
|
|-- repository
|   \-- maintenance
|       |-- MaintenanceEntity.java
|       \-- MaintenanceRepository.java
|
|-- service
|   |-- AvailabilityService.java
|   |-- BuildingService.java
|   |-- HealthService.java
|   |-- RoomService.java
|   \-- cache
|       \-- CacheWarmupService.java
|
\-- util
    |-- DateTimeUtils.java
    \-- StatusResolver.java
```

## Bootstrap class

`RoomStatusApplication.java` is currently located in:
`room-status/room-status/src/main/java/com/example/roomstatus/RoomStatusApplication.java`

## Notes

- Most classes are currently scaffolds (empty bodies).
- `application.yaml` defines cache names, actuator exposure, swagger path, and Google settings.
