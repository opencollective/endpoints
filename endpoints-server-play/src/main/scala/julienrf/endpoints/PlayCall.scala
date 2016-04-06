package julienrf.endpoints

import play.api.mvc.Call

class PlayCall extends Endpoints {

  type Path[A] = A => String

  def static(segment: String) = _ => segment

  def dynamic = (s: String) => s

  def chained[A, B](first: Path[A], second: Path[B])(implicit fc: FlatConcat[A, B]): Path[fc.Out] =
    (ab: fc.Out) => {
      val (a, b) = fc.unapply(ab)
      first(a) ++ "/" ++ second(b)
    }


  type Request[A] = A => Call
  type RequestEntity[A] = Unit

  type RequestMarshaller[A] = Unit

  def get[A](path: Path[A]) =
    a => Call("GET", path(a))

  def post[A, B](path: Path[A], entity: RequestEntity[B])(implicit fc: FlatConcat[A, B]): Request[fc.Out] =
    (ab: fc.Out) => {
      val (a, b) = fc.unapply(ab)
      Call("POST", path(a))
    }

  object request extends RequestApi {
    def jsonEntity[A : RequestMarshaller] = ()
  }


  type Response[A] = Unit

  type ResponseMarshaller[A] = Unit

  def jsonEntity[A](implicit ev: ResponseMarshaller[A]) = ()


  type Endpoint[I, O] = I => Call

  def endpoint[A, B](request: Request[A], response: Response[B]) = request

}
