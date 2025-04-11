package com.ll.playon.global.initData;

import com.ll.playon.domain.board.entity.Board;
import com.ll.playon.domain.board.entity.BoardComment;
import com.ll.playon.domain.board.entity.BoardLike;
import com.ll.playon.domain.board.enums.BoardCategory;
import com.ll.playon.domain.board.repository.BoardCommentRepository;
import com.ll.playon.domain.board.repository.BoardLikeRepository;
import com.ll.playon.domain.board.repository.BoardRepository;
import com.ll.playon.domain.chat.entity.PartyRoom;
import com.ll.playon.domain.chat.repository.PartyRoomRepository;
import com.ll.playon.domain.game.game.entity.*;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.repository.WeeklyGameRepository;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.entity.WeeklyPopularGuild;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guild.repository.WeeklyPopularGuildRepository;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoard;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardComment;
import com.ll.playon.domain.guild.guildBoard.entity.GuildBoardLike;
import com.ll.playon.domain.guild.guildBoard.enums.BoardTag;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardCommentRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardLikeRepository;
import com.ll.playon.domain.guild.guildBoard.repository.GuildBoardRepository;
import com.ll.playon.domain.guild.guildJoinRequest.entity.GuildJoinRequest;
import com.ll.playon.domain.guild.guildJoinRequest.enums.ApprovalState;
import com.ll.playon.domain.guild.guildJoinRequest.repository.GuildJoinRequestRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.service.SteamAsyncService;
import com.ll.playon.domain.party.party.entity.Party;
import com.ll.playon.domain.party.party.entity.PartyMember;
import com.ll.playon.domain.party.party.entity.PartyTag;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.type.PartyRole;
import com.ll.playon.domain.title.entity.Title;
import com.ll.playon.domain.title.entity.enums.ConditionType;
import com.ll.playon.domain.title.repository.TitleRepository;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final GuildJoinRequestRepository guildJoinRequestRepository;
    private final GuildBoardRepository guildBoardRepository;
    private final GuildBoardCommentRepository guildBoardCommentRepository;
    private final GuildBoardLikeRepository guildBoardLikeRepository;
    private final PartyRepository partyRepository;
    private final PasswordEncoder passwordEncoder;
    private final TitleRepository titleRepository;
    private final GameRepository gameRepository;
    private final PartyRoomRepository partyRoomRepository;
    private final SteamAsyncService steamAsyncService;

    @Autowired
    @Lazy
    private BaseInitData self;
    @Autowired
    private WeeklyGameRepository weeklyGameRepository;
    @Autowired
    private WeeklyPopularGuildRepository weeklyPopularGuildRepository;
    @Autowired
    private BoardCommentRepository boardCommentRepository;
    @Autowired
    private BoardLikeRepository boardLikeRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleGames();
            self.makeTitles();
            self.makeSampleUsers();
            self.makeSampleGuilds();
            self.makeSampleWeeklyPopularGames();
            self.makeSampleParties();
            self.makeSampleGuildJoinRequests();
            self.makeSampleGuildBoards();
            self.makeSampleWeeklyPopularGuild();
            self.makeSampleBoards();
        };
    }

    @Transactional
    public void makeSampleGames() {
        if (gameRepository.count() > 0) {
            return;
        }

        SteamGame game1 = SteamGame.builder()
                .name("sampleGame1")
                .appid(1L)
                .releaseDate(LocalDate.now())
                .headerImage(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/87bc28e442a9758e1c6f83bc531cf673a066e472/header_alt_assets_0.jpg?t=1743743917")
                .requiredAge(0)
                .developers("sample")
                .publishers("sample")
                .aboutTheGame("샘플 게임 데이터입니다.")
                .shortDescription("샘플 게임 데이터입니다.")
                .isSinglePlayer(true)
                .isMultiPlayer(true)
                .build();
        SteamImage img1 = SteamImage.builder()
                .game(game1)
                .screenshot(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/ss_31b5597fecf2d9a2904bc9bbf8011aacb18143db.600x338.jpg?t=1743743917")
                .build();
        game1.getScreenshots().add(img1);
        SteamMovie m1 = SteamMovie.builder()
                .game(game1)
                .movie("http://video.akamai.steamstatic.com/store_trailers/257118552/movie_max.mp4?t=1742961813")
                .build();
        game1.getMovies().add(m1);
        SteamGenre steamGenre = SteamGenre.builder()
                .name("Action")
                .build();
        steamGenre.getGames().add(game1);
        game1.getGenres().add(steamGenre);
        gameRepository.save(game1);

        SteamGame game2 = SteamGame.builder()
                .name("sampleGame2")
                .appid(2L)
                .releaseDate(LocalDate.now())
                .headerImage(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/87bc28e442a9758e1c6f83bc531cf673a066e472/header_alt_assets_0.jpg?t=1743743917")
                .requiredAge(0)
                .developers("sample")
                .publishers("sample")
                .aboutTheGame("샘플 게임 데이터입니다.")
                .shortDescription("샘플 게임 데이터입니다.")
                .isSinglePlayer(true)
                .isMultiPlayer(true)
                .build();
        SteamImage img2 = SteamImage.builder()
                .game(game2)
                .screenshot(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/ss_31b5597fecf2d9a2904bc9bbf8011aacb18143db.600x338.jpg?t=1743743917")
                .build();
        game2.getScreenshots().add(img2);
        SteamMovie m2 = SteamMovie.builder()
                .game(game2)
                .movie("http://video.akamai.steamstatic.com/store_trailers/257118552/movie_max.mp4?t=1742961813")
                .build();
        game2.getMovies().add(m2);
        steamGenre.getGames().add(game2);
        game2.getGenres().add(steamGenre);
        gameRepository.save(game2);

        SteamGame game3 = SteamGame.builder()
                .name("sampleGame3")
                .appid(3L)
                .releaseDate(LocalDate.now())
                .headerImage(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/87bc28e442a9758e1c6f83bc531cf673a066e472/header_alt_assets_0.jpg?t=1743743917")
                .requiredAge(0)
                .developers("sample")
                .publishers("sample")
                .aboutTheGame("샘플 게임 데이터입니다.")
                .shortDescription("샘플 게임 데이터입니다.")
                .isSinglePlayer(true)
                .isMultiPlayer(true)
                .build();
        SteamImage img3 = SteamImage.builder()
                .game(game3)
                .screenshot(
                        "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2246340/ss_31b5597fecf2d9a2904bc9bbf8011aacb18143db.600x338.jpg?t=1743743917")
                .build();
        game3.getScreenshots().add(img3);
        SteamMovie m3 = SteamMovie.builder()
                .game(game3)
                .movie("http://video.akamai.steamstatic.com/store_trailers/257118552/movie_max.mp4?t=1742961813")
                .build();
        game3.getMovies().add(m3);
        steamGenre.getGames().add(game3);
        game3.getGenres().add(steamGenre);
        gameRepository.save(game3);
    }

    @Transactional
    public void makeTitles() {
        if (titleRepository.count() > 0) {
            return;
        }

        titleRepository.saveAll(List.of(
                Title.builder().name("튜토리얼 클리어").description("회원 가입을 완료했다.")
                        .conditionType(ConditionType.REGISTERED).conditionValue(1).build(),
                Title.builder().name("초보 컬렉터").description("스팀 게임을 1개 소유했다.")
                        .conditionType(ConditionType.STEAM_GAME_COUNT).conditionValue(1).build(),
                Title.builder().name("열린 지갑").description("스팀 게임을 10개 소유했다.")
                        .conditionType(ConditionType.STEAM_GAME_COUNT).conditionValue(10).build(),
                Title.builder().name("스팀이라는 게임을 하는 것").description("스팀 게임을 100개 소유했다.")
                        .conditionType(ConditionType.STEAM_GAME_COUNT).conditionValue(100).build(),
                Title.builder().name("같이 게임해요!").description("파티에 처음 참여했다.")
                        .conditionType(ConditionType.PARTY_JOIN_COUNT).conditionValue(1).build(),
                Title.builder().name("나랑 게임할 사람!").description("파티를 처음 개설했다.")
                        .conditionType(ConditionType.PARTY_CREATE_COUNT).conditionValue(1).build(),
                Title.builder().name("파티 인싸").description("파티에 10번 참여했다.")
                        .conditionType(ConditionType.PARTY_JOIN_COUNT).conditionValue(10).build(),
                Title.builder().name("파티광").description("파티를 10번 개설했다.")
                        .conditionType(ConditionType.PARTY_CREATE_COUNT).conditionValue(10).build(),
                Title.builder().name("하루가 짧다").description("파티 참여 누적시간이 24시간을 넘었다.")
                        .conditionType(ConditionType.PARTY_TIME_ACCUMULATED).conditionValue(24).build(),
                Title.builder().name("나라가 허락한…").description("파티 참여 누적시간이 일주일을 넘었다.")
                        .conditionType(ConditionType.PARTY_TIME_ACCUMULATED).conditionValue(168).build(),
                Title.builder().name("로그ON").description("처음으로 파티로그를 작성했다.")
                        .conditionType(ConditionType.PARTY_LOG_WRITE_COUNT).conditionValue(1).build(),
                Title.builder().name("추억을 쓰는 자").description("파티로그를 10번 작성했다.")
                        .conditionType(ConditionType.PARTY_LOG_WRITE_COUNT).conditionValue(10).build(),
                Title.builder().name("칭찬합니다").description("MVP 투표에 처음으로 참여했다.")
                        .conditionType(ConditionType.MVP_VOTE_GIVEN).conditionValue(1).build(),
                Title.builder().name("인정협회 대법관").description("MVP 투표에 10번 참여했다.")
                        .conditionType(ConditionType.MVP_VOTE_GIVEN).conditionValue(10).build(),
                Title.builder().name("1인분은 합니다").description("MVP 추천을 처음 받았다.")
                        .conditionType(ConditionType.MVP_VOTE_RECEIVED).conditionValue(1).build(),
                Title.builder().name("버스기사 경력직").description("MVP 추천을 10번 받았다.")
                        .conditionType(ConditionType.MVP_VOTE_RECEIVED).conditionValue(10).build(),
                Title.builder().name("길드 마스터").description("길드를 개설했다.")
                        .conditionType(ConditionType.GUILD_CREATE).conditionValue(1).build(),
                Title.builder().name("가입인사드려요").description("길드 커뮤니티에 처음으로 글을 작성했다.")
                        .conditionType(ConditionType.GUILD_POST_COUNT).conditionValue(1).build(),
                Title.builder().name("길드 정회원").description("길드 커뮤니티에 10개의 글을 작성했다.")
                        .conditionType(ConditionType.GUILD_POST_COUNT).conditionValue(10).build(),
                Title.builder().name("선플 달기 운동").description("길드 커뮤니티에 처음으로 댓글을 달았다.")
                        .conditionType(ConditionType.GUILD_COMMENT_COUNT).conditionValue(1).build(),
                Title.builder().name("길드 분위기 메이커").description("길드 커뮤니티에 10개의 댓글을 달았다.")
                        .conditionType(ConditionType.GUILD_COMMENT_COUNT).conditionValue(10).build(),
                Title.builder().name("Dear Diary").description("자유게시판에 처음으로 글을 작성했다.")
                        .conditionType(ConditionType.BOARD_POST_COUNT).conditionValue(1).build(),
                Title.builder().name("여기가 인스타죠?").description("자유게시판에 10개의 글을 작성했다.")
                        .conditionType(ConditionType.BOARD_POST_COUNT).conditionValue(10).build(),
                Title.builder().name("무플 방지 운동").description("자유게시판에 처음으로 댓글을 달았다.")
                        .conditionType(ConditionType.BOARD_COMMENT_COUNT).conditionValue(1).build(),
                Title.builder().name("댓글 중독자").description("자유게시판에 10개의 댓글을 달았다.")
                        .conditionType(ConditionType.BOARD_COMMENT_COUNT).conditionValue(10).build()
        ));
    }

    @Transactional
    public void makeSampleUsers() {
        if (memberRepository.count() != 0) {
            return;
        }

        Member sampleMember1 = Member.builder()
                .steamId(123L).username("sampleUser1").nickname("sampleUser1").lastLoginAt(LocalDateTime.now())
                .role(Role.USER).build();
        memberRepository.save(sampleMember1);
        List<Long> gameAppIds = Arrays.asList(1L, 2L, 3L);
        steamAsyncService.saveUserGameList(gameAppIds, sampleMember1);

        Member sampleMember2 = Member.builder()
                .steamId(456L).username("sampleUser2").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember2);
        steamAsyncService.saveUserGameList(gameAppIds, sampleMember2);

        Member sampleMember3 = Member.builder()
                .steamId(789L).username("sampleUser3").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember3);

        steamAsyncService.saveUserGameList(gameAppIds, sampleMember3);

        Member noSteamMember = Member.builder()
                .username("noSteamMember").nickname("noSteamUser").password(passwordEncoder.encode("noSteam123"))
                .lastLoginAt(LocalDateTime.now()).role(Role.USER).preferredGenre("Adventure").build();
        memberRepository.save(noSteamMember);

        Member owner = Member.builder()
                .steamId(111L)
                .username("owner")
                .nickname("owner")
                .password(passwordEncoder.encode("owner"))
                .profileImg("")
                .password(passwordEncoder.encode("owner"))
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(owner);

        steamAsyncService.saveUserGameList(gameAppIds, owner);

        Member partyOwner = Member.builder()
                .steamId(555L)
                .username("partyOwner")
                .nickname("partyOwner")
                .profileImg("")
                .password(passwordEncoder.encode("partyOwner"))
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyOwner);

        steamAsyncService.saveUserGameList(gameAppIds, partyOwner);

        Member partyOwner2 = Member.builder()
                .steamId(556L)
                .username("partyOwner2")
                .nickname("partyOwner2")
                .profileImg("")
                .password(passwordEncoder.encode("partyOwner2"))
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyOwner2);

        steamAsyncService.saveUserGameList(gameAppIds, partyOwner2);

        Member partyMember = Member.builder()
                .steamId(2252L)
                .username("partyMember")
                .nickname("partyMember")
                .profileImg("")
                .password(passwordEncoder.encode("partyMember"))
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyMember);

        steamAsyncService.saveUserGameList(gameAppIds, partyMember);
    }

    @Transactional
    public void makeSampleGuilds() {
        if (guildRepository.count() != 0) {
            return;
        }
        List<Member> members = memberRepository.findAll();
        List<SteamGame> games = gameRepository.findAll();

        // 고정된 샘플 길드 데이터
        List<Guild> guilds = List.of(
                Guild.builder()
                        .owner(members.get(0))
                        .name("공개 길드 A")
                        .description("누구나 가입 가능한 길드")
                        .maxMembers(20)
                        .game(games.get(0))
                        .isPublic(true)
                        .isDeleted(false)
                        .build(),

                Guild.builder()
                        .owner(members.get(1))
                        .name("비공개 길드 B")
                        .description("비공개 길드입니다")
                        .maxMembers(10)
                        .game(games.get(1))
                        .isPublic(false)
                        .isDeleted(false)
                        .build(),

                Guild.builder()
                        .owner(members.get(2))
                        .name("DELETED_" + UUID.randomUUID().toString())
                        .description("DELETED")
                        .maxMembers(15)
                        .game(games.get(2))
                        .guildImg("DELETED")
                        .isPublic(true)
                        .isDeleted(true)
                        .build()
        );

        for (Guild guild : guilds) {
            guild.setGuildTags(createSampleGuildTags(guild));
            guildRepository.save(guild);

            // 길드장 등록
            guildMemberRepository.save(GuildMember.builder()
                    .guild(guild)
                    .member(guild.getOwner())
                    .guildRole(GuildRole.LEADER)
                    .build());

            // 추가 멤버 등록
            List<Member> extraMembers = members.stream()
                    .filter(m -> !m.equals(guild.getOwner()))
                    .limit(3) // 길드마다 최대 3명
                    .toList();

            GuildRole[] roles = {GuildRole.MANAGER, GuildRole.MEMBER};

            for (int i = 0; i < extraMembers.size(); i++) {
                guildMemberRepository.save(GuildMember.builder()
                        .guild(guild)
                        .member(extraMembers.get(i))
                        .guildRole(roles[i % roles.length])
                        .build());
            }
        }
    }

    @Transactional
    public void makeSampleParties() {
        if (this.partyRepository.count() != 0) {
            return;
        }

        // OWNER 후보 멤버 (5~8번 고정)
        List<Member> owners = memberRepository.findAll().stream()
                .filter(member -> member.getId() >= 5 && member.getId() <= 8)
                .sorted(Comparator.comparing(Member::getId)) // 순서를 보장하기 위해 정렬
                .toList();

        Map<Long, Member> memberMap = owners.stream().collect(Collectors.toMap(Member::getId, m -> m));
        List<TagType> tagTypes = new ArrayList<>(List.of(TagType.values()));
        List<SteamGame> steamGames = this.gameRepository.findAll(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();

        Random random = new Random();

        for (Member owner : owners) {
            for (int i = 0; i < 3; i++) {
                String randomName = "파티_" + UUID.randomUUID().toString().substring(0, 6);
                String randomDescription = "랜덤 파티 설명 " + UUID.randomUUID().toString().substring(0, 6);
                LocalDateTime randomPartyAt = LocalDateTime.now().plusDays(random.nextInt(30));
                boolean isPublic = random.nextBoolean();
                int minimum = random.nextInt(2, 10);
                int maximum = random.nextInt(10, 51);
                SteamGame steamGame = steamGames.get(random.nextInt(steamGames.size()));

                Party party = Party.builder()
                        .name(randomName)
                        .description(randomDescription)
                        .partyAt(randomPartyAt)
                        .publicFlag(isPublic)
                        .minimum(minimum)
                        .maximum(maximum)
                        .game(steamGame)
                        .build();

                // OWNER 등록
                PartyMember ownerMember = PartyMember.builder()
                        .member(owner)
                        .party(party)
                        .partyRole(PartyRole.OWNER)
                        .mvpPoint(0)
                        .build();
                party.addPartyMember(ownerMember);

                // 참가자 등록 - 다음 순번의 멤버
                long participantId = switch ((int) (long) owner.getId()) {
                    case 5 -> 6;
                    case 6 -> 7;
                    case 7 -> 8;
                    case 8 -> 5;
                    default -> throw new IllegalStateException("잘못된 멤버 ID");
                };
                Member participant = memberMap.get(participantId);

                PartyMember participantMember = PartyMember.builder()
                        .member(participant)
                        .party(party)
                        .partyRole(PartyRole.MEMBER)
                        .mvpPoint(0)
                        .build();
                party.addPartyMember(participantMember);

                // 태그 설정
                List<PartyTag> partyTags = new ArrayList<>();
                for (TagType tagType : tagTypes) {
                    List<TagValue> tagValuesForType = Arrays.stream(TagValue.values())
                            .filter(tv -> isValidTagValueForType(tv, tagType))
                            .toList();
                    TagValue randomTagValue = tagValuesForType.get(random.nextInt(tagValuesForType.size()));
                    partyTags.add(PartyTag.builder()
                            .party(party)
                            .type(tagType)
                            .value(randomTagValue)
                            .build());
                }
                party.setPartyTags(partyTags);

                partyRepository.save(party);

                PartyRoom partyRoom = PartyRoom.builder()
                        .party(party)
                        .build();

                partyRoomRepository.save(partyRoom);
            }
        }
    }

    // 특정 TagValue가 TagType에 맞는 값인지 확인하는 메서드
    private static final Map<TagType, Set<TagValue>> VALID_TAG_VALUE_MAP = Map.of(
            TagType.PARTY_STYLE, EnumSet.of(
                    TagValue.BEGINNER, TagValue.CASUAL, TagValue.NORMAL,
                    TagValue.HARDCORE, TagValue.EXTREME, TagValue.COMPLETIONIST, TagValue.SPEEDRUN),
            TagType.GAME_SKILL, EnumSet.of(
                    TagValue.MASTER, TagValue.HACKER, TagValue.PRO, TagValue.NEWBIE),
            TagType.GENDER, EnumSet.of(
                    TagValue.MALE, TagValue.FEMALE),
            TagType.SOCIALIZING, EnumSet.of(
                    TagValue.SOCIAL_FRIENDLY, TagValue.GAME_ONLY, TagValue.NO_CHAT)
    );

    private boolean isValidTagValueForType(TagValue tagValue, TagType tagType) {
        return VALID_TAG_VALUE_MAP.getOrDefault(tagType, Collections.emptySet()).contains(tagValue);
    }

    private List<GuildTag> createSampleGuildTags(Guild guild) {
        List<GuildTag> guildTags = new ArrayList<>();
        Random random = new Random();

        Map<TagType, List<TagValue>> tagTypeToValues = Map.of(
                TagType.PARTY_STYLE,
                List.of(TagValue.BEGINNER, TagValue.CASUAL, TagValue.NORMAL,
                        TagValue.HARDCORE, TagValue.EXTREME, TagValue.COMPLETIONIST, TagValue.SPEEDRUN),
                TagType.GAME_SKILL,
                List.of(TagValue.MASTER, TagValue.HACKER, TagValue.PRO,
                        TagValue.NEWBIE),
                TagType.GENDER, List.of(TagValue.MALE, TagValue.FEMALE),
                TagType.SOCIALIZING, List.of(TagValue.SOCIAL_FRIENDLY, TagValue.GAME_ONLY, TagValue.NO_CHAT)
        );

        for (TagType tagType : tagTypeToValues.keySet()) {
            List<TagValue> possibleValues = new ArrayList<>(tagTypeToValues.get(tagType));
            Collections.shuffle(possibleValues); // 랜덤화

            int tagCount = 1 + random.nextInt(Math.min(3, possibleValues.size())); // 1~3개
            for (int i = 0; i < tagCount; i++) {
                TagValue tagValue = possibleValues.get(i); // 중복 없이 선택

                GuildTag guildTag = GuildTag.builder()
                        .guild(guild)
                        .type(tagType)
                        .value(tagValue)
                        .build();

                guildTags.add(guildTag);
            }
        }

        return guildTags;
    }

    @Transactional
    public void makeSampleWeeklyPopularGames() {
        if (weeklyGameRepository.count() > 0) {
            return;
        }

        List<Long> gameAppIds = List.of(1L, 2L, 3L);

        List<LocalDate> weeks = List.of(
                LocalDate.of(2025, 3, 24),
                LocalDate.of(2025, 3, 31),
                LocalDate.of(2025, 4, 7)
        );

        Random random = new Random();
        for (LocalDate week : weeks) {
            for (int i = 0; i < 3; i++) {
                WeeklyPopularGame popularGame = WeeklyPopularGame.builder()
                        .appid(gameAppIds.get(i))
                        .playCount(10 + random.nextLong(20)) // 10 ~ 29 랜덤
                        .weekStartDate(week)
                        .build();

                weeklyGameRepository.save(popularGame);
            }
        }
    }

    @Transactional
    public void makeSampleWeeklyPopularGuild() {
        if (weeklyPopularGuildRepository.count() > 0) {
            return;
        }

        List<Long> guild = List.of(1L, 2L, 3L);

        List<LocalDate> weeks = List.of(
                LocalDate.of(2025, 3, 24),
                LocalDate.of(2025, 3, 31),
                LocalDate.of(2025, 4, 7)
        );

        Random random = new Random();
        for (LocalDate week : weeks) {
            for (int i = 0; i < 3; i++) {
                WeeklyPopularGuild popularGuild = WeeklyPopularGuild.builder()
                        .guildId(guild.get(i))
                        .postCount(10 + random.nextLong(20)) // 10 ~ 29 랜덤
                        .weekStartDate(week)
                        .build();

                weeklyPopularGuildRepository.save(popularGuild);
            }
        }
    }

    @Transactional
    public void makeSampleGuildJoinRequests() {
        if (guildJoinRequestRepository.count() > 0) {
            return;
        }

        List<Guild> guilds = guildRepository.findAll();
        List<Member> members = memberRepository.findAll();

        // 이미 길드에 속해있지 않은 멤버만 선택 (가입 요청 가능한 멤버들)
        List<Member> candidates = members.stream()
                .filter(member -> guildMemberRepository.findAllByMember(member).isEmpty())
                .limit(5) // 최대 5명만 예시로
                .toList();

        Random random = new Random();

        for (Member member : candidates) {
            Guild targetGuild = guilds.get(random.nextInt(guilds.size()));

            GuildJoinRequest request = GuildJoinRequest.builder()
                    .guild(targetGuild)
                    .member(member)
                    .approvalState(ApprovalState.PENDING)
                    .build();

            guildJoinRequestRepository.save(request);
        }
    }

    @Transactional
    public void makeSampleGuildBoards() {
        if (guildBoardRepository.count() > 0) {
            return;
        }

        List<Guild> guilds = guildRepository.findAll();
        Random random = new Random();

        for (Guild guild : guilds) {
            List<GuildMember> guildMembers = guildMemberRepository.findAllByGuild(guild);

            if (guildMembers.isEmpty()) {
                continue;
            }

            int boardCount = 2 + random.nextInt(2); // 2~3개 생성

            for (int i = 0; i < boardCount; i++) {
                GuildMember author = guildMembers.get(random.nextInt(guildMembers.size()));

                // 태그를 NOTICE로 줄 확률 (30%), 리더/매니저가 아닌 경우 강제로 FREE
                boolean isNotice = random.nextInt(100) < 30;
                BoardTag tag =
                        (isNotice && author.getGuildRole().isManagerOrLeader()) ? BoardTag.NOTICE : BoardTag.FREE;

                String title = (tag == BoardTag.NOTICE ? "[공지] " : "") + "샘플 게시글 " + UUID.randomUUID().toString()
                        .substring(0, 5);
                String content = "샘플 내용입니다.\n테스트용입니다.";

                GuildBoard board = GuildBoard.builder()
                        .guild(guild)
                        .author(author)
                        .title(title)
                        .content(content)
                        .tag(tag)
                        .imageUrl(null)
                        .build();

                guildBoardRepository.save(board);

                // 댓글 (0~2개)
                int commentCount = random.nextInt(3);
                for (int j = 0; j < commentCount; j++) {
                    GuildMember commenter = guildMembers.get(random.nextInt(guildMembers.size()));
                    GuildBoardComment comment = GuildBoardComment.builder()
                            .author(commenter)
                            .comment("샘플 댓글 " + (j + 1))
                            .build();
                    board.addComment(comment);
                    guildBoardCommentRepository.save(comment);
                }

                // 좋아요 (0~2개)
                List<GuildMember> likers = guildMembers.stream()
                        .limit(random.nextInt(3))
                        .toList();

                for (GuildMember liker : likers) {
                    GuildBoardLike like = GuildBoardLike.builder()
                            .guildMember(liker)
                            .build();
                    board.addLike(like);
                    guildBoardLikeRepository.save(like);
                    board.increaseLike();
                }
            }
        }
    }

    @Transactional
    public void makeSampleBoards() {
        if (boardRepository.count() > 0) {
            return;
        }

        List<Member> members = memberRepository.findAll().stream()
                .limit(3)
                .toList();

        List<String> titles = List.of(
                "자유게시판 샘플 제목1",
                "자유게시판 샘플 제목2",
                "자유게시판 샘플 제목3"
        );

        List<String> contents = List.of(
                "샘플 내용1입니다.",
                "샘플 내용2입니다.",
                "샘플 내용3입니다."
        );

        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            Member author = members.get(random.nextInt(members.size()));

            Board board = Board.builder()
                    .author(author)
                    .title(titles.get(i))
                    .content(contents.get(i))
                    .category(BoardCategory.DAILY)
                    .build();

            boardRepository.save(board);

            // 댓글 추가
            for (int j = 0; j < random.nextInt(3); j++) {
                BoardComment comment = BoardComment.builder()
                        .board(board)
                        .author(author)
                        .comment("댓글 " + (j + 1))
                        .build();
                board.addComment(comment);
                boardCommentRepository.save(comment);
            }

            // 좋아요 추가
            if (random.nextBoolean()) {
                BoardLike like = BoardLike.builder()
                        .board(board)
                        .member(author)
                        .build();
                boardLikeRepository.save(like);
                board.increaseLikeCount();
            }
        }
    }
}