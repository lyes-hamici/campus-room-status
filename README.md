# campus-room-status


```
src/main/java/com/example/roomstatus
│
├── RoomStatusApplication.java
│
├── config
│   ├── CacheConfig.java
│   ├── OpenApiConfig.java
│   ├── GoogleClientConfig.java
│   ├── AppProperties.java
│   └── WebConfig.java
│
├── controller
│   ├── BuildingController.java
│   ├── RoomController.java
│   └── HealthController.java
│
├── dto
│   ├── response
│   │   ├── BuildingListResponse.java
│   │   ├── RoomListResponse.java
│   │   ├── RoomDetailResponse.java
│   │   ├── RoomScheduleResponse.java
│   │   ├── HealthResponse.java
│   │   └── ApiErrorResponse.java
│   │
│   ├── common
│   │   ├── BuildingDto.java
│   │   ├── RoomDto.java
│   │   ├── RoomSummaryDto.java
│   │   ├── EventDto.java
│   │   └── FilterDto.java
│   │
│   └── request
│       └── RoomSearchRequest.java
│
├── model
│   ├── Room.java
│   ├── Building.java
│   ├── RoomEvent.java
│   ├── RoomStatus.java
│   └── RoomType.java
│
├── service
│   ├── BuildingService.java
│   ├── RoomService.java
│   ├── AvailabilityService.java
│   ├── HealthService.java
│   └── cache
│       └── CacheWarmupService.java
│
├── integration
│   └── google
│       ├── GoogleAdminDirectoryClient.java
│       ├── GoogleCalendarClient.java
│       ├── GoogleMapper.java
│       └── GoogleConnectionChecker.java
│
├── mapper
│   ├── BuildingMapper.java
│   ├── RoomMapper.java
│   └── EventMapper.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── RoomNotFoundException.java
│   ├── GoogleIntegrationException.java
│   ├── InvalidRequestException.java
│   └── ErrorCode.java
│
├── repository
│   └── maintenance
│       ├── MaintenanceRepository.java
│       └── MaintenanceEntity.java
│
└── util
    ├── DateTimeUtils.java
    └── StatusResolver.java
```