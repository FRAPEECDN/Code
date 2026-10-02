#!/bin/bash
set -euo pipefail

BOOTSTRAP="kafka:9092"
ADMIN="/config/admin.properties"

echo "Waiting for Kafka..."
for i in {1..60}; do
  if /opt/kafka/bin/kafka-broker-api-versions.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN" >/dev/null 2>&1; then
    break
  fi
  sleep 2
done

echo "Creating SCRAM user..."
/opt/kafka/bin/kafka-configs.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN" \
  --alter --add-config 'SCRAM-SHA-256=[iterations=4096,password=dev-secret]' \
  --entity-type users --entity-name dev

echo "Creating test topic..."
/opt/kafka/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN" \
  --create --if-not-exists --topic dev-test --partitions 2 --replication-factor 1 \
  --config retention.ms=86400000

echo "Creating ACLs for dev user..."
# Producer permissions on dev-test
/opt/kafka/bin/kafka-acls.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN" \
  --add --allow-principal User:dev --producer --topic dev-test

# Consumer permissions on dev-test and the test consumer group prefix
/opt/kafka/bin/kafka-acls.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN" \
  --add --allow-principal User:dev --consumer --topic dev-test --group 'dev-*'

echo "Kafka dev environment ready."
echo "Bootstrap: localhost:9094"
echo "Username:  dev"
echo "Password:  dev-secret"
echo "Topic:     dev-test (2 partitions)"
