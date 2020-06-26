## Kafka

- Active Mq를 사용하던 중 대용량 메세지 처리를 위해서 도입해 봄.
- 각 마이크로 서비스들의 의존성을 떨어트리고 데이터의 파이프 라인을 구축하기 위해서 도입.
- 모니터링 및 관리의 용이성을 위해 confluent를 사용함
- 기본적인 스키마 설계 Listener를 통한 consumer, template를 통한 publish 가능
- schema생성을 위해 avro 사용
- connect를 통한 elastic search로의 데이터 전달