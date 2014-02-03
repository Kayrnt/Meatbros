package helpers

import scala.util.Random

/**
 * User: Kayrnt
 * Date: 03/02/2014
 * Time: 23:14
 */
object Utils {

  val rand : Random = new Random()

  def shuffle[T](xs: List[T]): List[T] = xs match {
    case List() => List()
    case xs => {
      val i = rand.nextInt(xs.size);
      xs(i) :: shuffle(xs.take(i) ++ xs.drop(i+1))
    }
  }



}
