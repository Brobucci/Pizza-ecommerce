package com.pizzeria.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_address")
public class CustomerAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private AppUser customer;

	@Column(length = 50)
	private String label;

	@Column(length = 255, nullable = false)
	private String street;

	@Column(length = 100, nullable = false)
	private String city;

	@Column(name = "postal_code", length = 10)
	private String postalCode;

	@Column(length = 255)
	private String notes;

	@Column(name = "is_default")
	private Boolean isDefault = false;

	protected CustomerAddress() {
	}

	public CustomerAddress(AppUser customer, String street, String city) {
		this.customer = customer;
		this.street = street;
		this.city = city;
	}

	public Long getId() {
		return id;
	}

	public AppUser getCustomer() {
		return customer;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
}