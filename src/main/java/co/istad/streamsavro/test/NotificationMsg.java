package co.istad.streamsavro.test;

import lombok.Builder;

@Builder
public record NotificationMsg(
        String message,
        String timestamp
) {
}
