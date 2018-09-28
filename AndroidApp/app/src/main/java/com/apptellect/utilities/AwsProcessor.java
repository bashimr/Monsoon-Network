package com.apptellect.utilities;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SetObjectAclRequest;

import java.io.File;

public class AwsProcessor {

    public final static String ERROR = "ERROR";

    final static String BUCKET = "monsoon-network";
    final static String KEY = "AKIAJSNEOIYPUEJS4PDA";
    final static String SECRET = "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";
    final static String URL_BASE = "https://s3.amazonaws.com/monsoon-network/";


    public static String postFile(String fileName,File file)
    {
        //Basic Credentials with Key and Secret
        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        CannedAccessControlList controlList = CannedAccessControlList.PublicRead;

        //Put object on S3 Bucket
        PutObjectResult result = s3Client.putObject(BUCKET,fileName,file);

        //Canned Access Control request to make object with Public Read permission
        SetObjectAclRequest aclRequest = new SetObjectAclRequest(BUCKET,fileName,controlList);
        s3Client.setObjectAcl(aclRequest);
        if(result.getETag() != null)
            return URL_BASE + fileName;
        else
            return ERROR;
    }
}
