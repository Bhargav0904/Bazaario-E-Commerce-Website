package com.bazaario.entity;

// A Core Java enum instead of the plain "role" string the JDBC version used -
// gives you compile-time safety (no risk of a typo like "Admn") and a fixed set
// of valid values enforced by the type system itself.
public enum Role {
	CUSTOMER,
	ADMIN
}
