A project that provides [http://code.google.com/p/kryo/](kryo) serializers for some jdk types and some external libs like e.g. joda time.

# Provided serializers / supporting classes:

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


# Usage
To be able to use the serializers you have to add the jar (from downloads section) to you classpath. Unfortunately, it's not yet in any maven repo.

Then you have to register the custom serializers at the kryo instance. The following code snippet shows how this is done for serializers that can be registered statically (directly for a known class).

    kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer( kryo ) );
    kryo.register( Class.class, new ClassSerializer( kryo ) );
    kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
    kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
    kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
    kryo.register( Collections.singletonList( "" ).getClass(), new CollectionsSingletonListSerializer( kryo ) );
    kryo.register( Collections.singleton( "" ).getClass(), new CollectionsSingletonSetSerializer( kryo ) );
    kryo.register( Collections.singletonMap( "", "" ).getClass(), new CollectionsSingletonMapSerializer( kryo ) );
    kryo.register( Currency.class, new CurrencySerializer( kryo ) );
    kryo.register( GregorianCalendar.class, new GregorianCalendarSerializer() );
    kryo.register( InvocationHandler.class, new JdkProxySerializer( kryo ) );
    kryo.register( StringBuffer.class, new StringBufferSerializer( kryo ) );
    kryo.register( StringBuilder.class, new StringBuilderSerializer( kryo ) );
    UnmodifiableCollectionsSerializer.registerSerializers( kryo );
    SynchronizedCollectionsSerializer.registerSerializers( kryo );
    
    // custom serializers for non-jdk libs
    
    // register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
    kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer( kryo ) );
    // joda datetime
    kryo.register( DateTime.class, new JodaDateTimeSerializer() );
    // wicket
    kryo.register( MiniMap.class, new MiniMapSerializer( kryo ) );
    
The following code snippet shows how to use the `KryoReflectionFactorySupport` (can only be used with sun/oracly jdk!) and how other serializers are registered via the `newSerializer` method. If you don't want to use the `KryoReflectionFactorySupport` you can override the `newSerializer` method for your `new Kryo()` instance.

    final Kryo kryo = new KryoReflectionFactorySupport() {
        
        @Override
        public Serializer newSerializer(final Class clazz) {
            if ( EnumSet.class.isAssignableFrom( clazz ) ) {
                return new EnumSetSerializer( this );
            }
            if ( EnumMap.class.isAssignableFrom( clazz ) ) {
                return new EnumMapSerializer( this );
            }
            if ( SubListSerializer.canSerialize( clazz ) ) {
                return new SubListSerializer( this );
            }
            if ( ClassWithEvilDefaultConstructor.class.isAssignableFrom( type ) ) {
                // for this class we want to create new instances via ReflectionFactory, not by calling the default constructor
                // can only be used with sun/oracle jdk
                final ReferenceFieldSerializerReflectionFactorySupport result = new ReferenceFieldSerializerReflectionFactorySupport( _kryo, type );
                result.setIgnoreSyntheticFields( false );
                return result;
            }
            if ( copyCollectionsForSerialization ) {
                if ( Collection.class.isAssignableFrom( clazz ) ) {
                    return new CopyForIterateCollectionSerializer( this );
                }
                if ( Map.class.isAssignableFrom( clazz ) ) {
                    return new CopyForIterateMapSerializer( this );
                }
            }
            return super.newSerializer( clazz );
        }
        
        @Override
        public boolean handleUnregisteredClass( final Class<?> type ) {
            // see if the given class is a cglib proxy
            if ( CGLibProxySerializer.canSerialize( type ) ) {
                // register the CGLibProxySerializer for this class
                _kryo.register( type, _kryo.getRegisteredClass( CGLibProxySerializer.CGLibProxyMarker.class ) );
                return true;
            }
            return false;
        }
        
    };
    

# Where to get help
You can [https://github.com/inbox/new/magro](contact me via github) or [https://github.com/magro/kryo-serializers/issues](submit an issue).

# How to contribute
If you want to contribute to this project you can fork the [https://github.com/magro/kryo-serializers](sources on github), make your changes and submit a pull request. Alternatively you can [https://github.com/magro/kryo-serializers/issues](submit an issue) with a patch attached.
