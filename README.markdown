A project that shows how serialization works with kryo (http://code.google.com/p/kryo/) and adds serializers for some jdk types.

Provided serializers / supporting classes:
* ArraysAsListSerializer - serializer for lists created via Arrays#asList(Object...)
* ClassSerializer - for class instances
* CollectionsEmptyListSerializer - for Collections#EMPTY_LIST or lists created via Collections#emptyList()
* CollectionsEmptyMapSerializer - for Collections#EMPTY_MAP or maps created via Collections#emptyMap()
* CollectionsEmptySetSerializer - for Collections#EMPTY_SET or sets created via Collections#emptySet()
* CollectionsSingletonListSerializer - for lists created via Collections#singletonList(Object)
* CollectionsSingletonMapSerializer - for maps created via Collections#singletonMap(Object, Object)
* CollectionsSingletonSetSerializer - for sets created via Collections#singleton(Object)
* CopyForIterateCollectionSerializer - creates a copy of the source collection for writing object data.
* CopyForIterateMapSerializer - creates a copy of the source map for writing object data.
* CurrencySerializer - serializer for Currency
* EnumMapSerializer - serializer for EnumMap
* EnumSetSerializer - serializer for EnumSet
* GregorianCalendarSerializer - optimized serializer for (Gregorian)Calendar (24 bytes vs. 1323 bytes with FieldSerializer)
* JdkProxySerializer - for jdk proxies (proxies created via Proxy.newProxyInstance)
* KryoReflectionFactorySupport - kryo specialization that uses sun's ReflectionFactory to create new instances for classes without a default constructor
* ReferenceFieldSerializerReflectionFactorySupport - a ReferenceFieldSerializer specialization that uses sun's ReflectionFactory to create new instances
* StringBufferSerializer - optimized serializer for StringBuffer
* StringBuilderSerializer - optimized serializer for StringBuilder
* SubListSerializer - serializer for lists created via List#subList(int, int)
* SynchronizedCollectionsSerializer - for synchronized Collections and Maps created via Collections.synchronized*.
* UnmodifiableCollectionsSerializer - for unmodifiable Collections and Maps created via Collections.unmodifiable*.

* cglib/CGLibProxySerializer - serializer for CGLib proxies
* jodatime/JodaDateTimeSerializer - serializer for joda's DateTime
* wicket/MiniMapSerializer - serializer for wicket's MiniMap


For building you can use buildr (http://buildr.apache.org).