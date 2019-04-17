/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala.jdk

import java.util.{Optional, OptionalDouble, OptionalInt, OptionalLong}
import java.{lang => jl}

/** This object contains methods that convert between Scala `Option` and Java `Optional` types.
  *
  * The explicit conversion methods defined here are practical when writing Java code. For Scala
  * code, it is recommended to use the extension methods defined in [[OptionConverters.Ops]].
  *
  * Scala `Option` is extended with a `toJava` method that creates a corresponding `Optional`, and
  * a `toJavaPrimitive` method that creates a specialized variant (e.g., `OptionalInt`) if
  * applicable.
  *
  * Java `Optional` is extended with a `toScala` method and a `toJavaPrimitive` method.
  *
  * Finally, specialized `Optional` types are extended with `toScala` and `toJavaGeneric` methods.
  *
  * Example usage:
  *
  * {{{
  *   import scala.jdk.OptionConverters.Ops._
  *   val a = Option("example").toJava      // Creates java.util.Optional[String] containing "example"
  *   val b = (None: Option[String]).toJava // Creates an empty java.util.Optional[String]
  *   val c = a.toScala                     // Back to Option("example")
  *   val d = b.toScala                     // Back to None typed as Option[String]
  *   val e = Option(2.7).toJava            // java.util.Optional[Double] containing boxed 2.7
  *   val f = Option(2.7).toJavaPrimitive   // java.util.OptionalDouble containing 2.7 (not boxed)
  *   val g = f.toScala                     // Back to Option(2.7)
  *   val h = f.toJavaGeneric               // Same as e
  *   val i = e.toJavaPrimitive             // Same as f
  * }}}
  *
  * @define primitiveNote Note: this method uses the boxed type `java.lang.X` instead of the
  *                       primitive type `scala.X` to improve compatibility when using it in
  *                       Java code (the Scala compiler emits `C[Int]` as `C[Object]` in bytecode
  *                       due to [[https://github.com/scala/bug/issues/4214 scala/bug#4214]]). In
  *                       Scala code, add `import scala.jdk.OptionConverters.Ops._` and use the
  *                       extension methods instead.
  *
  */
object OptionConverters {

  /** This object provides extension methods that convert between Scala `Option` and Java `Optional`
    * types, see [[OptionConverters]].
    */
  object Ops {
    /** Provides conversions from Java `Optional` to Scala `Option` and specialized `Optional` types */
    implicit class RichOptional[A](private val o: java.util.Optional[A]) extends AnyVal {
      /** Convert a Java `Optional` to a Scala `Option` */
      def toScala: Option[A] = if (o.isPresent) Some(o.get) else None

      /** Convert a Java `Optional` to a Scala `Option` */
      @deprecated("Use `toScala` instead", "2.13.0")
      def asScala: Option[A] = if (o.isPresent) Some(o.get) else None

      /** Convert a generic Java `Optional` to a specialized variant */
      def toJavaPrimitive[O](implicit shape: OptionShape[A, O]): O = shape.fromJava(o)
    }

    /** Provides conversions from Scala `Option` to Java `Optional` types */
    implicit class RichOption[A](private val o: Option[A]) extends AnyVal {
      /** Convert a Scala `Option` to a generic Java `Optional` */
      def toJava: Optional[A] = o match { case Some(a) => Optional.ofNullable(a); case _ => Optional.empty[A] }

      /** Convert a Scala `Option` to a generic Java `Optional` */
      @deprecated("Use `toJava` instead", "2.13.0")
      def asJava: Optional[A] = o match { case Some(a) => Optional.ofNullable(a); case _ => Optional.empty[A] }

      /** Convert a Scala `Option` to a specialized Java `Optional` */
      def toJavaPrimitive[O](implicit shape: OptionShape[A, O]): O = shape.fromScala(o)
    }

    /** Provides conversions from `OptionalDouble` to Scala `Option` and the generic `Optional` */
    implicit class RichOptionalDouble(private val o: OptionalDouble) extends AnyVal {
      /** Convert a Java `OptionalDouble` to a Scala `Option` */
      def toScala: Option[Double] = if (o.isPresent) Some(o.getAsDouble) else None

      /** Convert a Java `OptionalDouble` to a Scala `Option` */
      @deprecated("Use `toScala` instead", "2.13.0")
      def asScala: Option[Double] = if (o.isPresent) Some(o.getAsDouble) else None

      /** Convert a Java `OptionalDouble` to a generic Java `Optional` */
      def toJavaGeneric: Optional[Double] = if (o.isPresent) Optional.of(o.getAsDouble) else Optional.empty[Double]
    }

    /** Provides conversions from `OptionalInt` to Scala `Option` and the generic `Optional` */
    implicit class RichOptionalInt(private val o: OptionalInt) extends AnyVal {
      /** Convert a Java `OptionalInt` to a Scala `Option` */
      def toScala: Option[Int] = if (o.isPresent) Some(o.getAsInt) else None

      /** Convert a Java `OptionalInt` to a Scala `Option` */
      @deprecated("Use `toScala` instead", "2.13.0")
      def asScala: Option[Int] = if (o.isPresent) Some(o.getAsInt) else None

      /** Convert a Java `OptionalInt` to a generic Java `Optional` */
      def toJavaGeneric: Optional[Int] = if (o.isPresent) Optional.of(o.getAsInt) else Optional.empty[Int]
    }

    /** Provides conversions from `OptionalLong` to Scala `Option` and the generic `Optional` */
    implicit class RichOptionalLong(private val o: OptionalLong) extends AnyVal {
      /** Convert a Java `OptionalLong` to a Scala `Option` */
      def toScala: Option[Long] = if (o.isPresent) Some(o.getAsLong) else None

      /** Convert a Java `OptionalLong` to a Scala `Option` */
      @deprecated("Use `toScala` instead", "2.13.0")
      def asScala: Option[Long] = if (o.isPresent) Some(o.getAsLong) else None

      /** Convert a Java `OptionalLong` to a generic Java `Optional` */
      def toJavaGeneric: Optional[Long] = if (o.isPresent) Optional.of(o.getAsLong) else Optional.empty[Long]
    }
  }

  /** Convert a Scala `Option` to a Java `Optional` */
  def toJava[A](o: Option[A]): Optional[A] = o match {
    case Some(a) => Optional.ofNullable(a)
    case _ => Optional.empty[A]
  }

  /** Convert a Scala `Option[Double]` to a Java `OptionalDouble`
    *
    * $primitiveNote
    */
  def toJavaOptionalDouble(o: Option[jl.Double]): OptionalDouble = o match {
    case Some(a) => OptionalDouble.of(a)
    case _ => OptionalDouble.empty
  }

  /** Convert a Scala `Option[Int]` to a Java `OptionalInt`
    *
    * $primitiveNote
    */
  def toJavaOptionalInt(o: Option[jl.Integer]): OptionalInt = o match {
    case Some(a) => OptionalInt.of(a)
    case _ => OptionalInt.empty
  }

  /** Convert a Scala `Option[Long]` to a Java `OptionalLong`
    *
    * $primitiveNote
    */
  def toJavaOptionalLong(o: Option[jl.Long]): OptionalLong = o match {
    case Some(a) => OptionalLong.of(a)
    case _ => OptionalLong.empty
  }

  /** Convert a Java `Optional` to a Scala `Option` */
  def toScala[A](o: Optional[A]): Option[A] = if (o.isPresent) Some(o.get) else None

  /** Convert a Java `OptionalDouble` to a Scala `Option`
    *
    * $primitiveNote
    */
  def toScala(o: OptionalDouble): Option[jl.Double] = if (o.isPresent) Some(o.getAsDouble) else None

  /** Convert a Java `OptionalInt` to a Scala `Option`
    *
    * $primitiveNote
    */
  def toScala(o: OptionalInt): Option[jl.Integer] = if (o.isPresent) Some(o.getAsInt) else None

  /** Convert a Java `OptionalLong` to a Scala `Option`
    *
    * $primitiveNote
    */
  def toScala(o: OptionalLong): Option[jl.Long] = if (o.isPresent) Some(o.getAsLong) else None
}
