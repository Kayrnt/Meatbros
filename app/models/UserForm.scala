package models

/**
 * User: Kayrnt
 * Date: 19/01/2014
 * Time: 15:52
 */
case class UserForm(isMale: Boolean,
                    broname: Option[String],
                    twitter: Option[String],
                    facebook: Option[String],
                    instagram: Option[String],
                    website: Option[String],
                    nationality: Option[String],
                    isActive: Boolean)

object UserForm {

  import play.api.data._
  import play.api.data.Forms._

  def textToBool(mapping : play.api.data.Mapping[String]) :
  play.api.data.Mapping[Boolean] = {
    mapping.transform[Boolean](
      s => {
//        println("textToBool S : "+s)
        if(s == "true") true else false
      },
      b => {
//        println("textToBool B : "+b)
        if(b)"true"else "false"
      })
  }

  def boolToText(b : Boolean) : String = {
//    println("boolean to text: "+b)
    if(b) "true" else "false"
  }

  def boolToText(mapping : play.api.data.Mapping[Boolean]) :
  play.api.data.Mapping[String] = {
    mapping.transform[String](b => if(b)"true"else "false",
      s => if(s == "yes") true else false)
  }

  val form = Form(
    mapping(
      "isMale" -> textToBool(text),
      "broname" -> optional(text),
      "twitter" -> optional(text),
      "facebook" -> optional(text),
      "instagram" -> optional(text),
      "website" -> optional(text),
      "nationality" -> optional(text),
      "isActive" -> textToBool(text)
    )(UserForm.apply)(UserForm.unapply)
  )

//  val anyData = Map("name" -> "bob", "age" -> "18")
//  val user: UserForm = userForm.bind(anyData).get

}