package com.epickur.entity

import java.time.LocalDateTime

import play.api.libs.json.Json

case class User(var userId: String = null,
				var name: String,
				var first: String,
				var last: String,
				var password: String,
				var email: String,
				var zipcode: String,
				var state: String,
				var country: String,
				var allow: Int = 0,
				var code: String = null,
				var key: String = null,
				var newPassword: String = null,
				var createdAt: LocalDateTime = LocalDateTime.now(),
				var updatedAt: LocalDateTime = LocalDateTime.now())

object User {
	// TODO create own write implementation to hide null field
	implicit val userToJson = Json.writes[User]
}
