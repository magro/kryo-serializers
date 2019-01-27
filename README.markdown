A project that provides [kryo](https://github.com/EsotericSoftware/kryo) (v2, v3, v4) serializers for some jdk types and some external libs like e.g. joda time.

[![Build Status](https://travis-ci.org/magro/kryo-serializers.png?branch=master)](https://travis-ci.org/magro/kryo-serializers)
[![Coverage Status](https://coveralls.io/repos/github/magro/kryo-serializers/badge.svg?branch=master)](https://coveralls.io/github/magro/kryo-serializers?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.javakaffee/kryo-serializers/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.javakaffee%22%20AND%20a%3Akryo-serializers)

# Provided serializers / supporting classes:

* ArraysAsListSerializer - serializer for lists created via Arrays#asList(Object...)
* CollectionsEmptyListSerializer - for Collections#EMPTY_LIST or lists created via Collections#emptyList()
* CollectionsEmptyMapSerializer - for Collections#EMPTY_MAP or maps created via Collections#emptyMap()
* CollectionsEmptySetSerializer - for Collections#EMPTY_SET or sets created via Collections#emptySet()
* CollectionsSingletonListSerializer - for lists created via Collections#singletonList(Object)
* CollectionsSingletonMapSerializer - for maps created via Collections#singletonMap(Object, Object)
* CollectionsSingletonSetSerializer - for sets created via Collections#singleton(Object)
* CopyForIterateCollectionSerializer - creates a copy of the source collection for writing object data.
* CopyForIterateMapSerializer - creates a copy of the source map for writing object data.
* DateSerializer - serializer for java.util.Date and subclasses (e.g. java.sql.Date, java.sql.Time, java.sql.Timestamp)
* BitSetSerializer - serializer for java.util.BitSet
* RegexSerializer - serializer for java.util.regex.Pattern
* URISerializer - serializer for java.net.URI
* UUIDSerializer - serializer for java.util.UUID
* EnumMapSerializer - serializer for EnumMap
* EnumSetSerializer - serializer for EnumSet
* UnicodeBlockSerializer - serializer for Character.UnicodeBlock
* FieldAnnotationAwareSerializer - field serializer that either ignores fields with user-specified annotations or exclusively considers such fields (e.g. useful to ignore all fields annotated with Springs `@Autowired` annotation).
* GregorianCalendarSerializer - optimized serializer for (Gregorian)Calendar (24 bytes vs. 1323 bytes with FieldSerializer)
* JdkProxySerializer - for jdk proxies (proxies created via Proxy.newProxyInstance)
* KryoReflectionFactorySupport - kryo specialization that uses sun's ReflectionFactory to create new instances for classes without a default constructor
* SubListSerializers - serializer for lists created via List#subList(int, int)
* SynchronizedCollectionsSerializer - for synchronized Collections and Maps created via Collections.synchronized*.
* UnmodifiableCollectionsSerializer - for unmodifiable Collections and Maps created via Collections.unmodifiable*.

* cglib/CGLibProxySerializer - serializer for CGLib proxies
* dexx/ListSerializer - serializer for dexx-collections' List
* dexx/SetSerializer - serializer for dexx collecttions' Set
* dexx/MapSerializer - serializer for dexx collections' Map
* guava/ArrayListMultimapSerializer - serializer for guava-libraries' ArrayListMultimap
* guava/ArrayTableSerializer - serializer for guava-libraries' ArrayTable
* guava/HashBasedTableSerializer - serializer for guava-libraries' HashBasedTable
* guava/HashMultimapSerializer -- serializer for guava-libraries' HashMultimap
* guava/ImmutableListSerializer - serializer for guava-libraries' ImmutableList
* guava/ImmutableSetSerializer - serializer for guava-libraries' ImmutableSet
* guava/ImmutableMapSerializer - serializer for guava-libraries' ImmutableMap
* guava/ImmutableMultimapSerializer - serializer for guava-libraries' ImmutableMultimap
* guava/ImmutableSortedSetSerializer - serializer for guava-libraries' ImmutableSortedSet
* guava/ImmutableTableSerializer - serializer for guava-libraries' ImmutableTable
* guava/LinkedHashMultimapSerializer - serializer for guava-libraries' LinkedHashMultimap
* guava/LinkedListMultimapSerializer - serializer for guava-libraries' LinkedListMultimap
* guava/ReverseListSerializer - serializer for guava-libraries' Lists.ReverseList / Lists.reverse
* guava/TreeBasedTableSerializer - serializer for guava-libraries' TreeBasedTable
* guava/TreeMultimapSerializer - serializer for guava-libraries' TreeMultimap
* guava/UnmodifiableNavigableSetSerializer - serializer for guava-libraries' UnmodifiableNavigableSet
* jodatime/JodaDateTimeSerializer - serializer for joda's DateTime
* jodatime/JodaIntervalSerializer - serializer for joda's Interval
* jodatime/JodaLocalDateSerializer - serializer for joda's LocalDate
* jodatime/JodaLocalDateTimeSerializer - serializer for joda's LocalDateTime
* jodatime/JodaLocalTimeSerializer - serializer for joda's LocalTime
* protobuf/ProtobufSerializer - serializer for protobuf GeneratedMessages
* wicket/MiniMapSerializer - serializer for wicket's MiniMap

# Usage
To be able to use the serializers you have to add the jar to your classpath. If your build tool support maven repositories you can use this dependency:

```xml
<dependency>
    <groupId>de.javakaffee</groupId>
    <artifactId>kryo-serializers</artifactId>
    <version>0.45</version>
</dependency>
```

It's available in maven central, so you don't need an additional repository definition.
If you're managing the classpath differently you can get the jar from the downloads section or [download from maven central](http://repo1.maven.org/maven2/de/javakaffee/kryo-serializers/).

After that's done you can register the custom serializers at the kryo instance. The following code snippet shows how this is done for serializers that can be registered statically (directly for a known class).

```java
kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
kryo.register( Collections.singletonList( "" ).getClass(), new CollectionsSingletonListSerializer() );
kryo.register( Collections.singleton( "" ).getClass(), new CollectionsSingletonSetSerializer() );
kryo.register( Collections.singletonMap( "", "" ).getClass(), new CollectionsSingletonMapSerializer() );
kryo.register( GregorianCalendar.class, new GregorianCalendarSerializer() );
kryo.register( InvocationHandler.class, new JdkProxySerializer() );
UnmodifiableCollectionsSerializer.registerSerializers( kryo );
SynchronizedCollectionsSerializer.registerSerializers( kryo );

// custom serializers for non-jdk libs

// register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer( kryo ) );
// dexx
ListSerializer.registerSerializers( kryo );
MapSerializer.registerSerializers( kryo );
SetSerializer.registerSerializers( kryo );
// joda DateTime, LocalDate, LocalDateTime and LocalTime
kryo.register( DateTime.class, new JodaDateTimeSerializer() );
kryo.register( LocalDate.class, new JodaLocalDateSerializer() );
kryo.register( LocalDateTime.class, new JodaLocalDateTimeSerializer() );
kryo.register( LocalDateTime.class, new JodaLocalTimeSerializer() );
// protobuf
kryo.register( SampleProtoA.class, new ProtobufSerializer() ); // or override Kryo.getDefaultSerializer as shown below
// wicket
kryo.register( MiniMap.class, new MiniMapSerializer() );
// guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap, ImmutableTable, ReverseList, UnmodifiableNavigableSet
ImmutableListSerializer.registerSerializers( kryo );
ImmutableSetSerializer.registerSerializers( kryo );
ImmutableMapSerializer.registerSerializers( kryo );
ImmutableMultimapSerializer.registerSerializers( kryo );
ImmutableTableSerializer.registerSerializers( kryo );
ReverseListSerializer.registerSerializers( kryo );
UnmodifiableNavigableSetSerializer.registerSerializers( kryo );
// guava ArrayListMultimap, HashMultimap, LinkedHashMultimap, LinkedListMultimap, TreeMultimap, ArrayTable, HashBasedTable, TreeBasedTable
ArrayListMultimapSerializer.registerSerializers( kryo );
HashMultimapSerializer.registerSerializers( kryo );
LinkedHashMultimapSerializer.registerSerializers( kryo );
LinkedListMultimapSerializer.registerSerializers( kryo );
TreeMultimapSerializer.registerSerializers( kryo );
ArrayTableSerializer.registerSerializers( kryo );
HashBasedTableSerializer.registerSerializers( kryo );
TreeBasedTableSerializer.registerSerializers( kryo );
```

The following code snippet shows how to use the `KryoReflectionFactorySupport` (can only be used with sun/oracle jdk!) and how other serializers are registered via the `getDefaultSerializer` lookup. If you don't want to use the `KryoReflectionFactorySupport` you can override the `getDefaultSerializer` method for your `new Kryo()` instance.

```java
final Kryo kryo = new KryoReflectionFactorySupport() {

    @Override
    public Serializer<?> getDefaultSerializer(final Class clazz) {
        if ( EnumSet.class.isAssignableFrom( clazz ) ) {
            return new EnumSetSerializer();
        }
        if ( EnumMap.class.isAssignableFrom( clazz ) ) {
            return new EnumMapSerializer();
        }
        if ( SubListSerializers.canSerialize( clazz ) ) {
            return SubListSerializers.createFor( clazz );
        }
        if ( copyCollectionsForSerialization ) {
            if ( Collection.class.isAssignableFrom( clazz ) ) {
                return new CopyForIterateCollectionSerializer();
            }
            if ( Map.class.isAssignableFrom( clazz ) ) {
                return new CopyForIterateMapSerializer();
            }
        }
        if ( Date.class.isAssignableFrom( type ) ) {
            return new DateSerializer( type );
        }
        // see if the given class is a cglib proxy
        if ( CGLibProxySerializer.canSerialize( type ) ) {
            // return the serializer registered for CGLibProxyMarker.class (see above)
            return getSerializer( CGLibProxySerializer.CGLibProxyMarker.class );
        }
        // protobuf
        if ( com.google.protobuf.GeneratedMessage.class.isAssignableFrom( type ) ) {
            return new ProtobufSerializer();
        }
        return super.getDefaultSerializer( clazz );
    }

};
```

# Where to get help
You can [contact me via github](https://github.com/inbox/new/magro) or [submit an issue](https://github.com/magro/kryo-serializers/issues).

# How to contribute
If you want to contribute to this project you can fork the [sources on github](https://github.com/magro/kryo-serializers), make your changes and submit a pull request. Alternatively you can [submit an issue](https://github.com/magro/kryo-serializers/issues) with a patch attached.
