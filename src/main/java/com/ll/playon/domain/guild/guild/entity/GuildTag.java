package com.ll.playon.domain.guild.guild.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import com.ll.playon.global.type.TagType;
import com.ll.playon.global.type.TagValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "guildTags",
        indexes = {
                @Index(name = "idx_guild_tag_type_value", columnList = "tagType, tagValue"),
                @Index(name = "idx_guild_tag_guild_id", columnList = "guild_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class GuildTag extends BaseTime {
        @ManyToOne(fetch = FetchType.LAZY)
        private Guild guild;

        @Enumerated(EnumType.STRING)
        @Column(name = "tagType")
        private TagType type;

        @Enumerated(EnumType.STRING)
        @Column(name = "tagValue")
        private TagValue value;
}
