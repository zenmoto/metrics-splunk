A [Splunk](http://www.splunk.com) reporter for [Metrics](http://metrics.codahale.com/).

```{Java}

ServiceArgs args = new ServiceArgs();
args.setUsername("admin");
args.setPassword("changeme");
Service splunk = Service.connect(args);

SplunkReporter
        .forRegistry(metricRegistry)
        .withSource("example-application")
        .addAttribute("some-tag", "foobar")
        .withIndex("my-index")
        .build(splunk)
        .start(10, TimeUnit.SECONDS);

```

