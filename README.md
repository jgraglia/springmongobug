# springmongobug (or misusage?)

See https://jira.spring.io/browse/DATAMONGO-1435

I'm facing a problem while trying to query a document with some dynamic properties (ie. not known in the Java entity)

See **ensure_can_find_by_dynamic_property_with_spring_mongo_api** and 


    java.lang.IllegalArgumentException: Owning type must not be null!

	at org.springframework.util.Assert.notNull(Assert.java:115)
        at org.springframework.data.mapping.PropertyPath.<init>(PropertyPath.java:70)
        at org.springframework.data.mapping.PropertyPath.create(PropertyPath.java:329)
        at org.springframework.data.mapping.PropertyPath.create(PropertyPath.java:309)
        at org.springframework.data.mapping.PropertyPath.create(PropertyPath.java:293)
        at org.springframework.data.mapping.PropertyPath.from(PropertyPath.java:275)
        at org.springframework.data.mongodb.core.convert.QueryMapper$MetadataBackedField.get

# Fix

I try to update mongo, spring mongo, spring, spring boot version with no luck.

I also try to remove Lombok (snif) or even to use @Document, but still no luck.

The pb was that I did not declare the generic types of my map. Therefore the null "owning type".

No pb with *genericDetails* !

    @Singular
    private final Map details;

    @Singular
    private final Map<String, Object> genericDetails;

And I can still use Lombok, and no @Document annotation!