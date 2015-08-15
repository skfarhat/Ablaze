package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;


@Entity
@Table (name="Category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@ElementCollection
	@CollectionTable(name="Link", joinColumns=@JoinColumn(name="category"))
	@Column(name="name")
	private Set<String> links; 

	public Category() {
		this.links = new HashSet<>(); 
	}

	public Category(String name) { 
		this.name = name; 
		this.links = new HashSet<>(); 
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getLinks() {
		return links; 
	} 

	public void addLink(String link) { 
		links.add(link); 
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false; 
		if (!obj.getClass().equals(getClass()))
			return false; 

		Category c = (Category) obj; 
		return c.getName().equals(getName()); 
	}
	@Override
	public String toString() {
		return name; 
	}
	public Integer getId() {
		return id;
	}
}
