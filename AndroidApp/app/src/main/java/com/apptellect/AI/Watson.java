package com.apptellect.AI;

import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImage;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Watson
{
    private static String Watson_Key = "mollC2xUKx9vZfN35TA81IauTNZGwI7K9LoywHO9fa4c";
    private static String Watson_Version = "2018-03-19";

    public static WatsonResponse isImageContains(String contextName, File file)
    {
        WatsonResponse response = new WatsonResponse();
        response.isValid = false;
        response.RequestedType = contextName;
        List<String> foundTypes = new ArrayList<>();
        try {
            IamOptions optionsIAM = new IamOptions.Builder()
                    .apiKey(Watson_Key)
                    .build();
            VisualRecognition service = new VisualRecognition(Watson_Version, optionsIAM);

            ClassifyOptions options = new ClassifyOptions.Builder()
                    .imagesFile(file)
                    .imagesFilename("test.jpg")
                    .build();

            ClassifiedImages result = service.classify(options).execute();
            List<ClassifiedImage> listClassifierImage = result.getImages();
            for (ClassifiedImage classifiedImage : listClassifierImage) {
                for (ClassifierResult classifierResult : classifiedImage.getClassifiers()) {
                    for (ClassResult classResult : classifierResult.getClasses()) {
                        if (classResult.getClassName().contains(contextName)) {
                            response.isValid = true;
                        }
                        else
                        {
                            foundTypes.add(classResult.getClassName());
                        }
                    }
                }
            }
            response.FoundTypes = foundTypes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(foundTypes.size() > 0 && foundTypes.size() <= 3)
        {
            String Value = "";
            for (String type : foundTypes)
            {
                Value += type + ", ";
            }
            response.ResponseMessage = removeLastChar(Value);
        }

        if(foundTypes.size() > 3)
        {
            String Value = "";
            for(int i=0; i<3; i++)
            {
                Value += foundTypes.get(i) + ", ";
            }
            response.ResponseMessage = removeLastChar(Value);
        }


        return response;
    }

    private static String removeLastChar(String str) {
    if (str != null && str.length() > 0 && str.charAt(str.length() - 2) == 'x') {
        str = str.substring(0, str.length() - 2);
    }
    return str;
}
}
