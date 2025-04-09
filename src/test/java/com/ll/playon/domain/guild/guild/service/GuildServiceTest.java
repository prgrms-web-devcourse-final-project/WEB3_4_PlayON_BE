package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.game.game.repository.GameRepository;
import com.ll.playon.domain.guild.guild.dto.request.GuildTagRequest;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.response.GetGuildDetailResponse;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildMemberRepositoryCustom;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.domain.title.service.TitleEvaluator;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.ServiceException;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuildServiceTest {

    @InjectMocks
    private GuildService guildService;

    @Mock
    private GuildRepository guildRepository;

    @Mock
    private GuildMemberRepository guildMemberRepository;

    @Mock
    private GuildMemberRepositoryCustom guildMemberRepositoryCustom;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private TitleEvaluator titleEvaluator;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = mock(Member.class);
    }

    @Test
    @DisplayName("길드 생성 실패 - 중복이름")
    void createGuild_fail_duplicate() {
        //given
        PostGuildRequest request = new PostGuildRequest(
                "중복용", "소개글", 10, true, 730L, "imgUrl", null
        );

        when(guildRepository.existsByName("중복용")).thenReturn(true);

        //when
        assertThatThrownBy(() -> guildService.createGuild(request, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_GUILD_NAME.getMessage());

        verify(guildRepository, never()).save(any(Guild.class));
        verify(guildMemberRepository, never()).save(any(GuildMember.class));
        //then

    }

    @Test
    @DisplayName("길드 수정 실패 - 권한 없음")
    void modifyGuild_fail_noPermission() {
        //given
        Guild guild = createGuild("기존길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);
        PutGuildRequest request = createPutGuildRequest("수정된이름");

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when/then
        assertThatThrownBy(() -> guildService.modifyGuild(1L, request, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NO_PERMISSION.getMessage());

        verify(guildRepository).findByIdAndIsDeletedFalse(1L);
        verify(guildRepository, never()).save(any());
    }

    @Test
    @DisplayName("길드 수정 실패 - 존재하지 않는 길드")
    void modifyGuild_fail_notFound() {
        // given
        Long guildId = 999L;
        PutGuildRequest request = createPutGuildRequest("수정된이름");

        when(guildRepository.findByIdAndIsDeletedFalse(guildId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> guildService.modifyGuild(guildId, request, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("길드 삭제 성공")
    void deleteGuild() {
        // given
        Long guildId = 1L;
        Guild guild = createGuild("삭제길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.LEADER);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        guildService.deleteGuild(guildId, mockMember);

        // then
        assertThat(guild.isDeleted()).isTrue();
        assertThat(guild.getName()).startsWith("DELETED_");
        assertThat(guild.getDescription()).isEqualTo("DELETED");
        assertThat(guild.getMembers()).isEmpty();

        verify(guildRepository).findWithTagsById(guildId);
        verify(guildMemberRepository).findByGuildAndMember(guild, mockMember);
    }

    @Test
    @DisplayName("길드 삭제 실패 - 권한 없음")
    void deleteGuild_fail_noPermission() {
        //given
        Guild guild = createGuild("삭제 길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when & then
        assertThatThrownBy(() -> guildService.deleteGuild(1L, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NO_PERMISSION.getMessage());

        // softDelete 호출 안 됨
        verify(guildRepository, never()).save(any());
    }

    @Test
    @DisplayName("길드 조회 성공 - 공개+멤버")
    void getGuildDetail_publicAndMember() {
        //given
        Guild guild = createGuild("테스트 길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("테스트 길드");
        assertThat(result.myRole()).isEqualTo("MEMBER");
    }

    @Test
    @DisplayName("길드 조회 성공 - 공개+멤버X")
    void getGuildDetail_publicAndNotMember() {
        //given
        Guild guild = createGuild("공개 길드", true);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.empty());

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("공개 길드");
        assertThat(result.myRole()).isEqualTo("GUEST");
    }

    @Test
    @DisplayName("길드 조회 성공 - 비공개+멤버")
    void getGuildDetail_privateAndMember() {
        // given
        Guild guild = createGuild("비공개 길드", false);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("비공개 길드");
        assertThat(result.myRole()).isEqualTo("MEMBER");
    }

    @Test
    @DisplayName("길드 조회 실패 - 비공개+멤버X")
    void getGuildDetail_privateAndNotMember() {
        // given
        Guild guild = createGuild("비공개 길드", false);

        when(guildRepository.findWithTagsById(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(any(Guild.class), eq(mockMember)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> guildService.getGuildDetail(1L, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NOT_FOUND.getMessage());

        verify(guildRepository).findWithTagsById(1L);
        verify(guildMemberRepository).findByGuildAndMember(any(Guild.class), eq(mockMember));
    }

    @Test
    @DisplayName("길드 조회 실패 - 삭제된 길드")
    void getGuildDetail_deleted_guild() {
        // given
        when(guildRepository.findWithTagsById(anyLong()))
                .thenReturn(Optional.empty()); // 삭제된 길드로 간주

        // when & then
        assertThatThrownBy(() -> guildService.getGuildDetail(1L, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NOT_FOUND.getMessage());

        verify(guildRepository).findWithTagsById(1L);
        verifyNoInteractions(guildMemberRepository); // 길드 조회 실패했으므로 멤버 조회 안 됨
    }

    private Guild createGuild(String name, boolean isPublic) {
        return Guild.builder()
                    .owner(mockMember)
                    .name(name)
                    .description("소개")
                    .maxMembers(10)
                    .isPublic(isPublic)
                    .guildImg("img.png")
                    .guildTags(new ArrayList<>(List.of(
                            GuildTag.builder()
                                    .type(TagType.PARTY_STYLE)
                                    .value(TagValue.HARDCORE)
                                    .build()
                    )))
                .build();
    }

    private GuildMember createGuildMember(Guild guild, GuildRole role) {
        return GuildMember.builder()
                .guild(guild)
                .member(mockMember)
                .guildRole(role)
                .build();
    }

    private PutGuildRequest createPutGuildRequest(String name) {
        return new PutGuildRequest(
                name,
                "수정된소개",
                15,
                730L,
                false,
                "new-img.png",
                getGuildTagRequests()
        );
    }

    private static List<GuildTagRequest> getGuildTagRequests() {
        return List.of(
                new GuildTagRequest("파티 스타일", "빡겜"),
                new GuildTagRequest("게임 실력", "뉴비"),
                new GuildTagRequest("성별", "남자만"),
                new GuildTagRequest("친목", "친목 환영")
        );
    }
}
