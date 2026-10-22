package com.group.cover_hub.model.entity;

import com.group.cover_hub.model.enums.CategoryTitle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryTitle categoryTitle;

    @OneToMany(mappedBy = "category")
    private List<Cover> covers = new ArrayList<>();
}
