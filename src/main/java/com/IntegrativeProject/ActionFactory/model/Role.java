package com.IntegrativeProject.ActionFactory.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private  Long id;

    @Column(name = "name")
    private  String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();

    public Role() {
    }

    public Role( String name) {

        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
