## scalacord - Development

I'm really glad you are interested in contributing to scalacord! This document will help you get started with
development of each module. This document is a work in progress, so if you have any questions, please
open an issue or contact me on Discord.

### `common`

Entity definitions, datatypes and serialization helpers for the Discord API. Built-in support for Scala JVM,
Scala Native, and Scala.js (Browser/Node.js).

To define a new entity or datatype, you must follow a simple pattern:

- Import `dev.axyria.scalacord.datatype.*` to get access to implicit serializers if you need types like
  `Optional`, `Color`, `BitSet` or `Snowflake`.
- Import `dev.axyria.scalacord.util.*` to get access to list-based serialization.
- Prefer using semi-auto derivation for serialization. Whenever a key doesn't match the _camelCase_ naming
  convention - that is, `snake_case` or any naming convention that contains special characters - use list-based
  serialization.
- List based serialization is simply constructing a `List[Option[EncodingContext[?]]]` and using `list.withOptional`
  to serialize it:
- - To add a type to the list you can use `("key", value).context` or `("key", value).optionContext`. Note that
    `optionContext` is only applied to `Optional` types, not `Option`.
  - You can join many contexts into a List by using `val elems = ("a", 1).context :: ("b", 2).context :: Nil`,
    which is the standard way of constructing a list in Scala. You can also use `List(...)`, but in order to
    make the code-style consistent, we use the `::` operator.
  - Finally, to return a `Json` element from our list of contexts, we use `list.withOptional`, even if the list
    doesn't contain any optional elements.
- Prefer using `ZeroType` for types that could be easily inlined, that is, types that can be compiled the inner
  value itself. For example a `object Hello extends ZeroType[Int]` means that `Hello(1)` will still be a Hello
  type, but it will be compiled as an Int - `1`. This is a design choice that every developer should make
  while coding in languages like Scala or Kotlin in order to reduce the performance overhead of using objects.
- Always export the encoder and decoder for your entity in its companion object.

### `gateway`

For event definitions:

- Follow the same entity definition pattern as `common` to define new events.
- After creating a new class or object containg the event, you must create a codec for it. It must replace
  the fields: `encoder` with the data encoder, `decoder` with the data decoder, `eventId` with the unique
  identifier for this event on the Gateway (it may be null for events with a different opcode), and
  `opcode` if the event has a different opcode than the default one (0).
- Finally, you must register its codec on the `Events` object by simply adding it to the `List` of events
  codecs.

For command definition:

- Follow the same entity definition pattern as `common` to define new commands.
- After creating a new class or object containg the command, you must create a codec for it. It must replace
  the fields: `encoder` with the data encoder, `decoder` with the data decoder, and `opcode` with the unique
  opcode for this command.
- Finally, you must register its codec on the `Commands` object by simply adding it to the `List` of command
  codecs.
