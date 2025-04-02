package com.ll.playon.domain.guild.guild.service;

import com.ll.playon.domain.guild.guild.dto.GuildMemberDto;
import com.ll.playon.domain.guild.guild.dto.request.GuildTagRequest;
import com.ll.playon.domain.guild.guild.dto.request.PostGuildRequest;
import com.ll.playon.domain.guild.guild.dto.request.PutGuildRequest;
import com.ll.playon.domain.guild.guild.dto.response.GetGuildDetailResponse;
import com.ll.playon.domain.guild.guild.dto.response.PostGuildResponse;
import com.ll.playon.domain.guild.guild.dto.response.PutGuildResponse;
import com.ll.playon.domain.guild.guild.entity.Guild;
import com.ll.playon.domain.guild.guild.entity.GuildTag;
import com.ll.playon.domain.guild.guild.repository.GuildMemberRepositoryCustom;
import com.ll.playon.domain.guild.guild.repository.GuildRepository;
import com.ll.playon.domain.guild.guildMember.entity.GuildMember;
import com.ll.playon.domain.guild.guildMember.enums.GuildRole;
import com.ll.playon.domain.guild.guildMember.repository.GuildMemberRepository;
import com.ll.playon.domain.member.entity.Member;
import com.ll.playon.global.exceptions.ErrorCode;
import com.ll.playon.global.exceptions.ServiceException;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import com.ll.playon.standard.page.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = mock(Member.class);
    }

    @Test
    @DisplayName("길드 생성 성공")
    void createGuild() {
        //given
        PostGuildRequest request = createPostGuildRequest("테스트용 길드1");
        Guild dummyGuild = Guild.createFrom(request, mockMember);

        when(guildRepository.existsByName("테스트용 길드1")).thenReturn(false);
        when(guildRepository.save(any(Guild.class))).thenReturn(dummyGuild);

        //when
        PostGuildResponse response = guildService.createGuild(request, mockMember);

        //then
        assertThat(response.name()).isEqualTo("테스트용 길드1");
        verify(guildRepository).save(any(Guild.class));
        verify(guildMemberRepository).save(any(GuildMember.class));
    }

    @Test
    @DisplayName("길드 생성 실패 - 중복이름")
    void createGuild_fail_duplicate() {
        //given
        PostGuildRequest request = new PostGuildRequest(
                "중복용", "소개글", 10, true, 3L, "imgUrl", null
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
    @DisplayName("길드 수정 성공")
    void modifyGuild_success() {
        // given
        Guild guild = createGuild("기존길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.LEADER);
        PutGuildRequest request = createPutGuildRequest("수정된이름");

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));
        when(guildRepository.existsByName("수정된이름")).thenReturn(false);

        // when
        PutGuildResponse response = guildService.modifyGuild(1L, request, mockMember);

        // then
        assertThat(response.name()).isEqualTo("수정된이름");
        assertThat(guild.getDescription()).isEqualTo("수정된소개");
        assertThat(guild.getMaxMembers()).isEqualTo(15);
        assertThat(guild.isPublic()).isFalse();
        assertThat(guild.getGuildImg()).isEqualTo("new-img.png");

        verify(guildRepository).findByIdAndIsDeletedFalse(1L);
        verify(guildRepository).save(any());
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

        when(guildRepository.findByIdAndIsDeletedFalse(guildId)).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        guildService.deleteGuild(guildId, mockMember);

        // then
        assertThat(guild.isDeleted()).isTrue();
        assertThat(guild.getName()).startsWith("DELETED_");
        assertThat(guild.getDescription()).isEqualTo("DELETED");
        assertThat(guild.getMembers()).isEmpty();

        verify(guildRepository).findByIdAndIsDeletedFalse(guildId);
        verify(guildMemberRepository).findByGuildAndMember(guild, mockMember);
    }

    @Test
    @DisplayName("길드 삭제 실패 - 권한 없음")
    void deleteGuild_fail_noPermission() {
        //given
        Guild guild = createGuild("삭제 길드", true);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
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

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("테스트 길드");
        assertThat(result.myRole()).isEqualTo(GuildRole.MEMBER);
    }

    @Test
    @DisplayName("길드 조회 성공 - 공개+멤버X")
    void getGuildDetail_publicAndNotMember() {
        //given
        Guild guild = createGuild("공개 길드", true);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.empty());

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("공개 길드");
        assertThat(result.myRole()).isNull();
    }

    @Test
    @DisplayName("길드 조회 성공 - 비공개+멤버")
    void getGuildDetail_privateAndMember() {
        // given
        Guild guild = createGuild("비공개 길드", false);
        GuildMember guildMember = createGuildMember(guild, GuildRole.MEMBER);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(guildMember));

        // when
        GetGuildDetailResponse result = guildService.getGuildDetail(1L, mockMember);

        // then
        assertThat(result.name()).isEqualTo("비공개 길드");
        assertThat(result.myRole()).isEqualTo(GuildRole.MEMBER);
    }

    @Test
    @DisplayName("길드 조회 실패 - 비공개+멤버X")
    void getGuildDetail_privateAndNotMember() {
        // given
        Guild guild = createGuild("비공개 길드", false);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> guildService.getGuildDetail(1L, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NOT_FOUND.getMessage());

        verify(guildRepository).findByIdAndIsDeletedFalse(1L);
        verify(guildMemberRepository).findByGuildAndMember(guild, mockMember);
    }

    @Test
    @DisplayName("길드 조회 실패 - 삭제된 길드")
    void getGuildDetail_deleted_guild() {
        // given
        when(guildRepository.findByIdAndIsDeletedFalse(anyLong()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> guildService.getGuildDetail(1L, mockMember))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ErrorCode.GUILD_NOT_FOUND.getMessage());

        verify(guildRepository).findByIdAndIsDeletedFalse(1L);
        verifyNoInteractions(guildMemberRepository); // 길드 조회 실패했으므로 멤버 조회 안 됨
    }

    @Test
    @DisplayName("멤버 조회 성공")
    void getGuildMembers() {
        // given
        Guild guild = createGuild("공개 길드", true);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.empty());

        List<GuildMember> mockMembers = List.of(
                GuildMember.builder().guild(guild).member(mock(Member.class)).guildRole(GuildRole.MEMBER).build()
        );
        Page<GuildMember> page = new PageImpl<>(mockMembers);

        when(guildMemberRepositoryCustom.findByGuildOrderByRoleAndCreatedAt(eq(guild), any())).thenReturn(page);

        // when
        PageDto<GuildMemberDto> result = guildService.getGuildMembers(1L, mockMember, PageRequest.of(0, 10));

        // then
        assertThat(result.items()).hasSize(1);
        verify(guildMemberRepositoryCustom).findByGuildOrderByRoleAndCreatedAt(eq(guild), any());
    }

    @DisplayName("멤버 조회 - 비공개+멤버")
    @Test
    void getGuildMembers_privateAndMember() {
        // given
        Guild guild = createGuild("비공개", false);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.of(
                GuildMember.builder().guild(guild).member(mockMember).guildRole(GuildRole.MEMBER).build()
        ));

        Page<GuildMember> page = new PageImpl<>(List.of());
        when(guildMemberRepositoryCustom.findByGuildOrderByRoleAndCreatedAt(eq(guild), any())).thenReturn(page);

        // when
        PageDto<GuildMemberDto> result = guildService.getGuildMembers(1L, mockMember, PageRequest.of(0, 10));

        // then
        assertThat(result.items()).isEmpty();
    }

    @DisplayName("멤버 조회 실패 - 비공개+멤버X")
    @Test
    void getGuildMembers_privateAndNotMember() {
        // given
        Guild guild = createGuild("비공개", false);

        when(guildRepository.findByIdAndIsDeletedFalse(anyLong())).thenReturn(Optional.of(guild));
        when(guildMemberRepository.findByGuildAndMember(guild, mockMember)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> guildService.getGuildMembers(1L, mockMember, PageRequest.of(0, 10)))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("길드 권한이 없습니다.");

        verify(guildMemberRepositoryCustom, never()).findByGuildOrderByRoleAndCreatedAt(any(), any());
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

    private PostGuildRequest createPostGuildRequest(String name) {
        return new PostGuildRequest(
                name,
                "소개글",
                10,
                true,
                1L,
                "imgUrl",
                getGuildTagRequests()
        );
    }

    private PutGuildRequest createPutGuildRequest(String name) {
        return new PutGuildRequest(
                name,
                "수정된소개",
                15,
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
