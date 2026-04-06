package com.sidework.project.application.port.in;

public record ProjectRetrospectiveCommand(
	String role,
	String strengths,
	String improvements
) {}
