package com.epickur.api.entities

import java.time.LocalDateTime

import com.epickur.api.utils.Implicits
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import play.api.libs.json._

case class Caterer(var id: Option[String] = None,
				   var name: String,
				   var description: String,
				   var manager: String,
				   var email: String,
				   var phoneNumber: PhoneNumber,
				   var location: Location,
				  // TODO next task add working time
				   //var workingTimes: WorkingTimes,
				   var createdBy: String,
				   var createdAt: Option[LocalDateTime] = None,
				   var updatedAt: Option[LocalDateTime] = None)

case class Location(var address: Address, var geo: Geo)

case class Address(var label: String,
				   var houseNumber: String,
				   var street: String,
				   var city: String,
				   var postalCode: Int,
				   var state: String,
				   var country: String)

case class Geo(var _type: String, var coordinates: Array[Double])

case class WorkingTimes(var hours: Hours,
						var minimumPreparationTime: Int)

case class Hours(var mon: Seq[TimeFrame],
				 var tue: Seq[TimeFrame],
				 var wed: Seq[TimeFrame],
				 var thu: Seq[TimeFrame],
				 var fri: Seq[TimeFrame],
				 var sat: Seq[TimeFrame],
				 var sun: Seq[TimeFrame])

case class TimeFrame(var open: Int, var close: Int)

object Caterer {
	implicit val phoneNumberToJson = Implicits.phoneNumberToJson
	implicit val geoToJsonWeb: OWrites[Geo] = new OWrites[Geo] {
		def writes(geo: Geo): JsObject = Json.obj("type" -> geo._type,"coordinates" -> geo.coordinates)
	}
	implicit val addressToJsonWeb: OWrites[Address] = Json.writes[Address]
	implicit val locationToJsonWeb: OWrites[Location] = Json.writes[Location]
	implicit val workingTimesToJsonWeb: OWrites[WorkingTimes] = Json.writes[WorkingTimes]
	implicit val hoursToJsonWeb: OWrites[Hours] = Json.writes[Hours]
	implicit val timeFrameToJsonWeb: OWrites[TimeFrame] = Json.writes[TimeFrame]
	val catererToJsonWeb: OWrites[Caterer] = Json.writes[Caterer]
	val catererToJsonDB: OWrites[Caterer] = new OWrites[Caterer] {
		def writes(caterer: Caterer): JsObject = generateJsonForCaterer(caterer)
	}

	implicit val jsonToPhoneNumber = Implicits.jsonToPhoneNumber
	val jsonToCatererWeb: Reads[Caterer] = new Reads[Caterer] {
		def reads(json: JsValue): JsResult[Caterer] = {
			for {
				id <- {
					val id = (json \ "id").validateOpt[String]
					if (id.isSuccess && id.get.isDefined) id else (json \ "_id").validateOpt[String]
				}
				name <- (json \ "name").validate[String]
				description <- (json \ "description").validate[String]
				manager <- (json \ "manager").validate[String]
				email <- (json \ "email").validate[String]
				phoneNumber <- (json \ "phoneNumber").validate[PhoneNumber]
				location <- (json \ "location").validate[Location]
				//workingTimes <- (json \ "workingTimes").validate[WorkingTimes]
				createdBy <- (json \ "createdBy").validate[String]
				createdAt <- (json \ "createdAt").validateOpt[LocalDateTime]
				updatedAt <- (json \ "updatedAt").validateOpt[LocalDateTime]
			} yield {
				Caterer(id, name, description, manager, email, phoneNumber, location/*, workingTimes*/, createdBy, createdAt, updatedAt)
			}
		}
	}
	implicit val jsonToLocationWeb: Reads[Location] = new Reads[Location] {
		def reads(json: JsValue): JsResult[Location] = {
			for {
				address <- (json \ "address").validate[Address]
				geo <- (json \ "geo").validate[Geo]
			} yield Location(address, geo)
		}
	}

	implicit val jsonToAddressWeb: Reads[Address] = new Reads[Address] {
		def reads(json: JsValue): JsResult[Address] = {
			for {
				label <- (json \ "label").validate[String]
				houseNumber <- (json \ "houseNumber").validate[String]
				street <- (json \ "street").validate[String]
				city <- (json \ "city").validate[String]
				postalCode <- (json \ "postalCode").validate[Int]
				state <- (json \ "state").validate[String]
				country <- (json \ "country").validate[String]
			} yield Address(label, houseNumber, street, city, postalCode, state, country)
		}
	}

	implicit val jsonToGeoWeb: Reads[Geo] = new Reads[Geo] {
		def reads(json: JsValue): JsResult[Geo] = {
			for {
				_type <- (json \ "type").validate[String]
				geo <- (json \ "coordinates").validate[Array[Double]]
			} yield Geo(_type, geo)
		}
	}

	implicit val jsonToWorkingTimes: Reads[WorkingTimes] = new Reads[WorkingTimes] {
		def reads(json: JsValue): JsResult[WorkingTimes] = {
			for {
				hours <- (json \ "hours").validate[Hours]
				minimumPreparationTime <- (json \ "minimumPreparationTime").validate[Int]
			} yield WorkingTimes(hours, minimumPreparationTime)
		}
	}

	implicit val jsonToHours: Reads[Hours] = new Reads[Hours] {
		def reads(json: JsValue): JsResult[Hours] = {
			for {
				mon <- (json \ "mon").validate[Seq[TimeFrame]]
				tue <- (json \ "tue").validate[Seq[TimeFrame]]
				wed <- (json \ "wed").validate[Seq[TimeFrame]]
				thu <- (json \ "thu").validate[Seq[TimeFrame]]
				fri <- (json \ "fri").validate[Seq[TimeFrame]]
				sat <- (json \ "sat").validate[Seq[TimeFrame]]
				sun <- (json \ "sun").validate[Seq[TimeFrame]]
			} yield Hours(mon, tue, wed, thu, fri, sat, sun)
		}
	}

	implicit val jsonToTimeFrame: Reads[TimeFrame] = new Reads[TimeFrame] {
		def reads(json: JsValue): JsResult[TimeFrame] = {
			for {
				open <- (json \ "open").validate[Int]
				close <- (json \ "close").validate[Int]
			} yield TimeFrame(open, close)
		}
	}

	def getJsonUpdatedCaterer(caterer: Caterer): JsObject = {
		Json.obj("$set" -> generateJsonForCaterer(caterer).-("createdAt"))
	}

	private def generateJsonForCaterer(caterer: Caterer): JsObject = {
		var json = Json.obj(
			"_id" -> caterer.id,
			"name" -> caterer.name,
			"description" -> caterer.description,
			"manager" -> caterer.manager,
			"email" -> caterer.email,
			"phoneNumber" -> Json.toJson(caterer.phoneNumber),
			"location" -> Json.toJson(caterer.location),
			//"workingTimes" -> Json.toJson(caterer.workingTimes),
			"createdBy" -> caterer.createdBy
		)
		if (caterer.createdAt.isDefined)
			json = json + ("createdAt" -> Json.toJson(caterer.createdAt.get))
		if (caterer.updatedAt.isDefined)
			json = json + ("updatedAt" -> Json.toJson(caterer.updatedAt.get))
		json
	}
}
