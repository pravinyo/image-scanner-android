# document-scanner
MVP app for document scanner

# To run this project locally

1. create `github.properties` file in `common` module inside project.  
   add `gpr.usr` and `gpr.key`
   gpr.use => username/owner name of the image-scanner library
   gpr.key => personal access token.

2. There are 2 ways to make this project work.
    1. Clone image-scanner repository in your github account and trigger the build.
        - this step is easy just click clone repository
        - update the publishing information in `build.gradle.kts` file
        - trigger the publish workflow
        - use this new package path in `common/build.gradle` by replaceing `dev.pravin:image-scanner:XXX` to your new path.
        - create one ready package personal access token and add in `gpr.key`
    
    2. Use below PAT token, to access package from my account.
        - Just add below token for `gpr.key` in github.properties  
          Token: `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`

# Image scanner repo details:          
It uses image scanner (https://github.com/pravinyo/image-scanner) for image processing and handling
