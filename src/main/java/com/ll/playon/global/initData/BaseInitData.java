package com.ll.playon.global.initData;

import com.ll.playon.domain.game.game.entity.SteamGame;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.member.entity.enums.Role;
import com.ll.playon.domain.member.repository.MemberRepository;
import com.ll.playon.domain.member.service.MemberService;
import com.ll.playon.domain.party.party.dto.request.PartyTagRequest;
import com.ll.playon.domain.party.party.dto.request.PostPartyRequest;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import com.ll.playon.domain.party.party.service.PartyService;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;
    private final GuildRepository guildRepository;
    private final GuildMemberRepository guildMemberRepository;
    private final MemberService memberService;
    private final PartyService partyService;
    private final PartyRepository partyRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameRepository gameRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleUsers();
            self.makeSampleGuilds();
            self.makeSampleParties();
        };
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
        List<Long> gameAppIds = Arrays.asList(730L, 570L);
        memberService.saveUserGameList(gameAppIds, sampleMember1);

        Member sampleMember2 = Member.builder()
                .steamId(456L).username("sampleUser2").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember2);
        memberService.saveUserGameList(gameAppIds, sampleMember2);

        Member sampleMember3 = Member.builder()
                .steamId(789L).username("sampleUser3").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember3);

        memberService.saveUserGameList(gameAppIds, sampleMember3);

        Member noSteamMember = Member.builder()
                .username("noSteamMember").nickname("noSteamUser").password(passwordEncoder.encode("noSteam123"))
                .lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(noSteamMember);

        Member owner = Member.builder()
                .steamId(111L)
                .username("owner")
                .nickname("owner")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(owner);

        memberService.saveUserGameList(gameAppIds, owner);

        Member partyOwner = Member.builder()
                .steamId(555L)
                .username("partyOwner")
                .nickname("partyOwner")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyOwner);

        memberService.saveUserGameList(gameAppIds, partyOwner);

        Member partyOwner2 = Member.builder()
                .steamId(556L)
                .username("partyOwner2")
                .nickname("partyOwner2")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyOwner2);

        memberService.saveUserGameList(gameAppIds, partyOwner2);

        Member partyMember = Member.builder()
                .steamId(2252L)
                .username("partyMember")
                .nickname("partyMember")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(partyMember);

        memberService.saveUserGameList(gameAppIds, partyMember);
    }

    @Transactional
    public void makeSampleGuilds() {
        if (gameRepository.count() == 0) {
            return;
        }

        if(guildRepository.count() != 0) {
            return;
        }

        List<Member> members = memberRepository.findAll().stream()
                .limit(3)
                .collect(Collectors.toList());

        List<Long> gameIds = List.of(252490L, 570L, 730L); // 샘플 SteamGame ID들
        List<SteamGame> games = gameRepository.findAllByAppidIn(gameIds);

        List<String> guildNames = List.of(
                "게임 마스터즈", "프로 플레이어스", "데빌 헌터즈",
                "판타지 길드", "엘리트 스쿼드", "레전드 레이더스",
                "나이트 워리어스", "프리스타일 게이머즈", "드래곤 슬레이어즈",
                "캐주얼 크루"
        );

        List<String> descriptions = List.of(
                "하드코어 게이머들의 모임",
                "캐주얼하게 즐기는 길드",
                "전략과 협동을 중시하는 길드입니다.",
                "PVP 특화 길드입니다.",
                "스피드런을 즐기는 길드입니다.",
                "초보자 환영 길드입니다.",
                "밤에 주로 활동하는 길드입니다.",
                "도전과제를 함께 하는 길드입니다.",
                "친목을 중시하는 길드입니다.",
                "경쟁을 좋아하는 사람들의 길드입니다."
        );

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Member owner = members.get(random.nextInt(members.size()));
            SteamGame selectedGame = games.get(random.nextInt(games.size()));

            Guild guild = Guild.builder()
                    .owner(owner)
                    .name(guildNames.get(i))
                    .description(descriptions.get(i))
                    .maxMembers(5 + random.nextInt(16))
                    .game(selectedGame)
                    .guildImg("a.png")
                    .isPublic(random.nextBoolean())
                    .isDeleted(false)
                    .build();

            List<GuildTag> sampleGuildTags = createSampleGuildTags(guild);
            guild.setGuildTags(sampleGuildTags);
            guildRepository.save(guild);

            // 길드장 저장
            guildMemberRepository.save(GuildMember.builder()
                    .guild(guild)
                    .member(owner)
                    .guildRole(GuildRole.LEADER)
                    .build());

            // 후보 멤버 리스트에서 owner 제외
            List<Member> candidates = new ArrayList<>(members);
            candidates.remove(owner);
            Collections.shuffle(candidates);

            // 추가 멤버 수 랜덤 (0 ~ 2명)
            int memberCount = random.nextInt(3);
            int added = 0;

            for (Member candidate : candidates) {
                if (added >= memberCount) {
                    break;
                }

                // 이미 같은 길드에 들어간 멤버는 패스
                GuildMember guildMember = GuildMember.builder()
                        .guild(guild)
                        .member(candidate)
                        .guildRole(random.nextBoolean() ? GuildRole.MANAGER : GuildRole.MEMBER)
                        .build();
                guildMemberRepository.save(guildMember);
                added++;
            }
        }
    }

    @Transactional
    public void makeSampleParties() {
        if (this.partyRepository.count() != 0) {
            return;
        }

        // 모든 멤버를 가져옵니다.
        List<Member> members = memberRepository.findAll().stream()
                .skip(Math.max(0, this.memberRepository.findAll().size() - 4))  // 뒤에서 4명 가져오기
                .toList();

        // 태그 타입과 태그 값들
        List<TagType> tagTypes = new ArrayList<>(List.of(TagType.values()));

        // 각 멤버에 대해 3개의 파티 생성
        for (Member member : members) {
            for (int i = 0; i < 3; i++) {
                // 랜덤 값 생성
                String randomName = "파티_" + UUID.randomUUID().toString().substring(0, 6);  // 랜덤 이름 생성
                String randomDescription = "랜덤 파티 설명 " + UUID.randomUUID().toString().substring(0, 6);  // 랜덤 설명
                LocalDateTime randomPartyAt = LocalDateTime.now().plusDays(new Random().nextInt(30));  // 30일 이내의 랜덤 날짜
                boolean isPublic = new Random().nextBoolean();  // 공개 여부 랜덤
                int minimum = new Random().nextInt(2, 10);  // 최소 인원 2명 이상
                int maximum = new Random().nextInt(10, 51);  // 최대 인원 10명 이상, 50명 이하
                Long gameId = (long) (new Random().nextInt(1, 10));  // 랜덤 게임 ID (나중에 게임 엔티티로 연결)

                // 파티 태그 생성
                List<PartyTagRequest> randomTags = new ArrayList<>();

                // 각 TagType에 대해 하나씩 TagValue를 선택하여 추가
                for (TagType tagType : tagTypes) {
                    // 해당 TagType에 맞는 TagValue들을 필터링
                    List<TagValue> tagValuesForType = Arrays.stream(TagValue.values())
                            .filter(tv -> isValidTagValueForType(tv, tagType))
                            .toList();

                    // TagType에 맞는 TagValue를 랜덤으로 1개 선택
                    TagValue randomTagValue = tagValuesForType.get(new Random().nextInt(tagValuesForType.size()));

                    // PartyTagRequest 생성 후 randomTags에 추가
                    randomTags.add(new PartyTagRequest(tagType.getKoreanValue(), randomTagValue.getKoreanValue()));
                }

                // PostPartyRequest 객체 생성
                PostPartyRequest postPartyRequest = new PostPartyRequest(
                        randomName,
                        randomDescription,
                        randomPartyAt,
                        isPublic,
                        minimum,
                        maximum,
                        gameId,
                        randomTags
                );

                // 파티 생성
                this.partyService.createParty(member, postPartyRequest);
            }
        }
    }

    // 특정 TagValue가 TagType에 맞는 값인지 확인하는 메서드
    private boolean isValidTagValueForType(TagValue tagValue, TagType tagType) {
        return switch (tagType) {
            case PARTY_STYLE ->
                    tagValue == TagValue.HARDCORE || tagValue == TagValue.CASUAL || tagValue == TagValue.SPEEDRUN
                    || tagValue == TagValue.COMPLETIONIST;
            case GAME_SKILL -> tagValue == TagValue.ROTTEN_WATER || tagValue == TagValue.STAGNANT_WATER
                               || tagValue == TagValue.MUD_WATER || tagValue == TagValue.CLEAN_WATER
                               || tagValue == TagValue.NEWBIE;
            case GENDER -> tagValue == TagValue.MALE || tagValue == TagValue.FEMALE;
            case SOCIALIZING -> tagValue == TagValue.SOCIAL_FRIENDLY || tagValue == TagValue.GAME_ONLY
                                || tagValue == TagValue.NO_CHAT;
        };
    }

    private List<GuildTag> createSampleGuildTags(Guild guild) {
        List<GuildTag> guildTags = new ArrayList<>();
        Random random = new Random();

        Map<TagType, List<TagValue>> tagTypeToValues = Map.of(
                TagType.PARTY_STYLE,
                List.of(TagValue.HARDCORE, TagValue.CASUAL, TagValue.SPEEDRUN, TagValue.COMPLETIONIST),
                TagType.GAME_SKILL,
                List.of(TagValue.ROTTEN_WATER, TagValue.STAGNANT_WATER, TagValue.MUD_WATER, TagValue.CLEAN_WATER,
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
}