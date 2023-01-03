package com.gokkan.gokkan.domain.auction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gokkan.gokkan.domain.auction.domain.Auction;
import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.FilterListRequest;
import com.gokkan.gokkan.domain.auction.domain.dto.AuctionDto.ListResponse;
import com.gokkan.gokkan.domain.auction.domain.type.AuctionStatus;
import com.gokkan.gokkan.domain.auction.domain.type.SortType;
import com.gokkan.gokkan.domain.category.domain.Category;
import com.gokkan.gokkan.domain.category.repository.CategoryRepository;
import com.gokkan.gokkan.domain.expertComment.domain.ExpertComment;
import com.gokkan.gokkan.domain.expertComment.repository.ExpertCommentRepository;
import com.gokkan.gokkan.domain.item.domain.Item;
import com.gokkan.gokkan.domain.item.repository.ItemRepository;
import com.gokkan.gokkan.domain.item.type.State;
import com.gokkan.gokkan.domain.member.domain.Member;
import com.gokkan.gokkan.domain.member.repository.MemberRepository;
import com.gokkan.gokkan.domain.style.domain.Style;
import com.gokkan.gokkan.domain.style.domain.StyleItem;
import com.gokkan.gokkan.domain.style.repository.StyleItemRepository;
import com.gokkan.gokkan.domain.style.repository.StyleRepository;
import com.gokkan.gokkan.global.querydsl.config.QueryDslConfig;
import com.gokkan.gokkan.global.security.oauth.entity.ProviderType;
import com.gokkan.gokkan.global.security.oauth.entity.Role;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
class AuctionRepositoryTest {

	String styleName1 = "style1";
	String styleName2 = "style2";
	String categoryName1 = "test category1";
	String categoryName2 = "test category2";

	@Autowired
	private AuctionRepository auctionRepository;
	@Autowired
	private ExpertCommentRepository expertCommentRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private StyleItemRepository styleItemRepository;
	@Autowired
	private StyleRepository styleRepository;
	@Autowired
	private MemberRepository memberRepository;


	@DisplayName("01_00. searchAllFilter")
	@Test
	public void test_01_00() {
		//given
		Category category1 = getCategory(categoryName1);
		Category category2 = getCategory(categoryName2);
		Style style1 = getStyle(this.styleName1);
		Style style2 = getStyle(this.styleName2);
		Member member = getMember("member", "member@test.com");

		Item item1 = getItem(category1, member, State.COMPLETE);
		item1.setStyleItems(
			new ArrayList<>(List.of(getStyleItem(style1, item1))));
		Item item2 = getItem(category2, member, State.COMPLETE);
		item2.setStyleItems(
			new ArrayList<>(List.of(getStyleItem(style2, item2))));
		Item item3 = getItem(category1, member, State.COMPLETE);
		item3.setStyleItems(
			new ArrayList<>(List.of(getStyleItem(style1, item3), getStyleItem(style2, item3))));
		Item item4 = getItem(category2, member, State.COMPLETE);
		item4.setStyleItems(
			new ArrayList<>(List.of(getStyleItem(style1, item4), getStyleItem(style2, item4))));

		ExpertComment expertComment1 = getExpertComment(item1);
		ExpertComment expertComment2 = getExpertComment(item2);
		ExpertComment expertComment3 = getExpertComment(item3);
		ExpertComment expertComment4 = getExpertComment(item4);

		Auction auction1 = getAuction(expertComment1, member);
		Auction auction2 = getAuction(expertComment2, member);
		Auction auction3 = getAuction(expertComment3, member);
		Auction auction4 = getAuction(expertComment4, member);

		//when

		List<ListResponse> listResponses1 = auctionRepository.searchAllFilter(
			getFilterListRequest(category1, List.of(styleName1)));
		List<ListResponse> listResponses2 = auctionRepository.searchAllFilter(
			getFilterListRequest(category2, List.of(styleName2)));
		List<ListResponse> listResponses3 = auctionRepository.searchAllFilter(
			getFilterListRequest(category1, null));
		List<ListResponse> listResponses4 = auctionRepository.searchAllFilter(
			getFilterListRequest(category2, null));

		//then
		assertEquals(listResponses1.size(), 2);
		assertEquals(listResponses2.size(), 2);
		assertEquals(listResponses3.size(), 2);
		assertEquals(listResponses4.size(), 2);
	}

	private FilterListRequest getFilterListRequest(Category category1, List<String> styleNames) {
		return FilterListRequest.builder()
			.sort(SortType.DESC)
			.styles(styleNames)
			.category(category1)
			.build();
	}


	private Auction getAuction(ExpertComment expertComment, Member member) {
		return auctionRepository.save(
			Auction.builder()
				.startDateTime(LocalDateTime.now())
				.endDateTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
				.startPrice(100L)
				.currentPrice(200L)
				.auctionStatus(AuctionStatus.STARTED)
				.expertComment(expertComment)
				.member(member)
				.build());
	}

	private ExpertComment getExpertComment(Item item) {
		return expertCommentRepository.save(
			ExpertComment.builder()
				.comment("test comment")
				.minPrice(100L)
				.maxPrice(400L)
				.expertInfo(null)
				.item(item)
				.build());
	}

	private Member getMember(String userId, String email) {
		return memberRepository.save(Member.builder()
			.userId(userId)
			.email(email)
			.name("name")
			.providerType(ProviderType.KAKAO)
			.role(Role.ADMIN)
			.build());
	}

	private Category getCategory(String name) {
		return categoryRepository.save(Category.builder()
			.name(name)
			.children(new ArrayList<>())
			.build());
	}

	private Item getItem(Category category, Member member, State state) {
		return itemRepository.save(Item.builder()
			.name("test name")
			.member(member)
			.category(category)
			.startPrice(100L)
			.width(100L)
			.depth(100L)
			.height(100L)
			.material("나무")
			.conditionGrade("test CG")
			.conditionDescription("test CD")
			.text("test text")
//			.madeIn("test madeIn")
			.designer("test designer")
			.brand("test brand")
			.productionYear(2023)
			.state(state)
			.created(LocalDateTime.now())
			.updated(LocalDateTime.now())
			.imageItems(new ArrayList<>())
			.imageChecks(new ArrayList<>())
			.styleItems(new ArrayList<>())
			.build());
	}

	private StyleItem getStyleItem(Style style, Item item) {
		return styleItemRepository.save(StyleItem.builder()
			.style(style)
			.name(style.getName())
			.item(item)
			.build());
	}

	private Style getStyle(String name) {
		return styleRepository.save(
			Style.builder()
				.name(name)
				.build());
	}
}