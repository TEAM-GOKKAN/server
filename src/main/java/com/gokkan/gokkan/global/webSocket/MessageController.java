package com.gokkan.gokkan.global.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

	private final SimpMessageSendingOperations simpMessageSendingOperations;

	@MessageMapping("/test/{auctionId}/{price}")
	public void test(@DestinationVariable Long auctionId, @DestinationVariable Long price) {

		String message = "auctionId : " + auctionId + "의 경매가 현재가 : " + price + "원으로 변경되었습니다.";
		simpMessageSendingOperations.convertAndSend("/topic/" + auctionId, message);
	}

	@MessageMapping("/test2")
	public void test2(Message message) {

		String text = "auctionId : " + message.getAuctionId() + "의 경매가 현재가 : " + message.getPrice()
			+ "원으로 변경되었습니다.";
		simpMessageSendingOperations.convertAndSend("/topic/" + message.getAuctionId(), text);
	}
}
