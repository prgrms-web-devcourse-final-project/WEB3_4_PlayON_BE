package com.ll.playon.global.initData;

import com.ll.playon.domain.game.game.entity.*;
import com.ll.playon.domain.game.game.repository.GameGenreRepository;
import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.game.game.repository.GenreRepository;
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
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreRepository gameGenreRepository;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.makeSampleUsers();
            self.makeSampleGuilds();
            self.makeSampleSteamGames();
        };
    }

    @Transactional
    public void makeSampleSteamGames() {
        if (gameRepository.count() > 0) return;

        SteamGenre genre1 = SteamGenre.builder().name("Action").build();
        SteamGenre genre2 = SteamGenre.builder().name("Free To Play").build();
        genreRepository.saveAll(List.of(genre1, genre2));

        List<SteamGame> games = new ArrayList<>();

        SteamGame game1 = SteamGame.builder()
                .appid(730L)
                .name("Counter-Strike 2")
                .headerImage("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/730/header.jpg")
                .requiredAge(0)
                .aboutTheGame("20년 넘게 이어진 경쟁 FPS")
                .shortDescription("정통 FPS 게임")
                .isWindowsSupported(true)
                .isMacSupported(false)
                .isLinuxSupported(true)
                .releaseDate(LocalDate.of(2012, 8, 21))
                .website("http://counter-strike.net/")
                .developers("Valve")
                .publishers("Valve")
                .build();

        game1.setScreenshots(List.of(
                SteamImage.builder().game(game1).screenshot("https://.../ss_1.jpg").build()
        ));
        game1.setMovies(List.of(
                SteamMovie.builder().game(game1).movie("http://video.akamai.../movie1.mp4").build()
        ));

        SteamGame game2 = SteamGame.builder()
                .appid(570L)
                .name("Dota 2")
                .headerImage("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/570/header.jpg")
                .requiredAge(0)
                .aboutTheGame("수백만의 유저가 매일 플레이")
                .shortDescription("팀 기반 전략 게임")
                .isWindowsSupported(true)
                .isMacSupported(true)
                .isLinuxSupported(true)
                .releaseDate(LocalDate.of(2013, 7, 9))
                .website("http://www.dota2.com/")
                .developers("Valve")
                .publishers("Valve")
                .build();

        game2.setScreenshots(List.of(
                SteamImage.builder().game(game2).screenshot("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/570/ss_ad8eee787704745ccdecdfded2ac57e9d2211e9.jpg").build(),
                SteamImage.builder().game(game2).screenshot("https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/570/ss_02b0c95702c2e8e91c183e6402d39112c48abf70.jpg").build()
        ));

        game2.setMovies(List.of(
                SteamMovie.builder().game(game2).movie("http://video.akamai.steamstatic.com/store_trailers/256692021/movie_max.mp4?t=1739210452").build(),
                SteamMovie.builder().game(game2).movie("http://video.akamai.steamstatic.com/store_trailers/256692021/movie_480p.mp4?t=1739210452").build()
        ));

        games.add(game1);
        games.add(game2);

        gameRepository.saveAll(games);

        GameGenre gameGenre1 = GameGenre.builder().game(game1).genre(genre1).build();
        GameGenre gameGenre2 = GameGenre.builder().game(game1).genre(genre2).build();
        GameGenre gameGenre3 = GameGenre.builder().game(game2).genre(genre1).build();
        gameGenreRepository.saveAll(List.of(gameGenre1, gameGenre2, gameGenre3));
    }

    @Transactional
    public void makeSampleUsers() {
        if (memberRepository.count() != 0) {
            return;
        }

        Member sampleMember1 = Member.builder()
                .steamId(123L).username("sampleUser1").nickname("sampleUser").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember1);
        List<Long> gameAppIds = Arrays.asList(730L, 570L);
        memberService.saveUserGameList(gameAppIds, sampleMember1);

        Member sampleMember2 = Member.builder()
                .steamId(456L).username("sampleUser2").nickname("sampleUser").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
        memberRepository.save(sampleMember2);
        memberService.saveUserGameList(gameAppIds, sampleMember2);

        Member sampleMember3 = Member.builder()
                .steamId(789L).username("sampleUser3").nickname("sampleUser").lastLoginAt(LocalDateTime.now()).role(Role.USER).build();
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

        Member user1 = Member.builder()
                .steamId(1515L)
                .username("user1")
                .nickname("user1")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(user1);

        memberService.saveUserGameList(gameAppIds, user1);

        Member user2 = Member.builder()
                .steamId(2222L)
                .username("user2")
                .nickname("user2")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(user2);

        memberService.saveUserGameList(gameAppIds, user2);

        Member user3 = Member.builder()
                .steamId(3333L)
                .username("user3")
                .nickname("user3")
                .profileImg("")
                .lastLoginAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        memberRepository.save(user3);

        memberService.saveUserGameList(gameAppIds, user3);
    }

    @Transactional
    public void makeSampleGuilds() {
        if(guildRepository.count() != 0) return;

        List<Member> members = memberRepository.findAll().stream()
                .limit(3)
                .collect(Collectors.toList());

        List<Long> gameIds = List.of(789L, 570L, 730L);

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

            Guild guild = Guild.builder()
                    .owner(owner)
                    .name(guildNames.get(i))
                    .description(descriptions.get(i))
                    .maxMembers(5 + random.nextInt(16))
                    .game(gameIds.get(random.nextInt(gameIds.size())))
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
                if (added >= memberCount) break;

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

    private List<GuildTag> createSampleGuildTags(Guild guild) {
        List<GuildTag> guildTags = new ArrayList<>();
        Random random = new Random();

        Map<TagType, List<TagValue>> tagTypeToValues = Map.of(
                TagType.PARTY_STYLE, List.of(TagValue.HARDCORE, TagValue.CASUAL, TagValue.SPEEDRUN, TagValue.COMPLETIONIST),
                TagType.GAME_SKILL, List.of(TagValue.ROTTEN_WATER, TagValue.STAGNANT_WATER, TagValue.MUD_WATER, TagValue.CLEAN_WATER, TagValue.NEWBIE),
                TagType.GENDER, List.of(TagValue.MALE, TagValue.FEMALE),
                TagType.SOCIALIZING, List.of(TagValue.SOCIAL_FRIENDLY, TagValue.GAME_ONLY, TagValue.NOC_CHAT)
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