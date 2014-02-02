package controllers


import play.api.mvc._


import play.api.libs.json._
import play.api.libs.json.Json
import models.Person


/**
 * User: Kayrnt
 * Date: 03/08/13
 * Time: 13:01
 */
object Upload extends Controller with securesocial.core.SecureSocial {

  def fileUploader = SecuredAction(parse.multipartFormData) {
    request =>
      Person.findByIdentity(request.user).map {
        person =>
          request.body.file("files[]").headOption.map {
            picture =>
              import java.io.File

              if (picture.contentType.getOrElse("") == "image/gif") {

                val uuid = java.util.UUID.randomUUID().toString

                val file = new File(s"static/gifs/$uuid.gif")

                val parent: File = file.getParentFile
                if (!parent.exists && !parent.mkdirs) {
                  throw new IllegalStateException("Couldn't create dir: " + parent)
                }

                picture.ref.moveTo(file)

                person.broGifUuid.map {
                  oldUuid =>
                    val oldPicture = new File(s"static/gifs/$oldUuid.gif")
                    oldPicture.delete()
                }

                Person.update(person.copy(broGifUuid = Some(uuid)))

                val json = Json.toJson(
                  Json.obj(
                    "files" -> Json.arr(
                      Json.obj(
                        "name" -> JsString("Bro picture"),
                        "size" -> JsNumber(file.length()),
                        "url" -> JsString(s"/static/gifs/$uuid.gif"),
                        "thumbnailUrl" -> JsString(s"/static/gifs/$uuid.gif")
                      )
                    ))
                )

                Ok(json)
              }
              else Redirect(routes.BroController.account).flashing(
                "error" -> "Unrecognized file type"
              )
          }
      }.flatten.getOrElse {
        Redirect(routes.BroController.account).flashing(
          "error" -> "Missing file"
        )
      }
  }

  def fileUploaderGet = Action {
    Ok("upload servlet")
  }

}