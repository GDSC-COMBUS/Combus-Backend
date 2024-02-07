package combus.backend.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleVisionOcrService {

    // 이미지 파일이 저장될 디렉토리 경로
    private static final String DIR_PATH = "/Users/jihyun/Documents";

    // Google Credentials 인스턴스
    private final GoogleCredentials credentials;

    public GoogleVisionOcrService() throws IOException {
        this.credentials = GoogleCredentials.fromStream(
                GoogleVisionOcrService.class.getClassLoader().getResourceAsStream("combus-413101-2872fecd0b70.json")
        );
    }

    // 이미지 파일을 로컬에 저장
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty())
            return null;

        String originalName = file.getOriginalFilename(); // 파일 원래 이름
        String uuid = UUID.randomUUID().toString(); // 파일 식별자
        String extension = originalName.substring(originalName.lastIndexOf(".")); // 파일 확장자 추출
        String savedName = uuid + extension; // 이미지 파일의 새로운 이름
        String savedPath = DIR_PATH + savedName; // 파일 경로

        file.transferTo(new File(savedPath)); // local에 파일 저장

        return savedPath;
    }

    // 이미지 파일로부터 텍스트 추출 및 숫자만 추출
    public String extractTextAndNumbersFromImage(String imagePath) throws IOException {
        // 이미지에서 텍스트 추출
        String extractedText = extractTextFromImage(imagePath);

        // 추출된 텍스트에서 숫자만 추출하여 반환
        return extractNumbersFromString(extractedText);
    }

    // 이미지 파일로부터 텍스트 추출
    public String extractTextFromImage(String imagePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.copyFrom(Files.readAllBytes(Paths.get(imagePath)));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build())) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            StringBuilder result = new StringBuilder();
            for (AnnotateImageResponse res : responses) {
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    result.append(annotation.getDescription()).append("\n"); // 각 행은 새 줄로 구분
                }
            }
            return result.toString();
        }
    }

    // 숫자만 추출하는 메서드
    public String extractNumbersFromString(String text) {
        StringBuilder numbers = new StringBuilder();
        // 정규표현식을 사용하여 문자열에서 숫자만 추출
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            numbers.append(matcher.group()).append("\n"); // 각 숫자를 새 줄로 구분하여 추가
        }
        return numbers.toString();
    }
}

//2번

//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.vision.v1.*;
//import com.google.protobuf.ByteString;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class GoogleVisionOcrService {
//
//    // 이미지 파일이 저장될 디렉토리 경로
//    private static final String DIR_PATH = "/Users/jihyun/Documents";
//
//    // Google Credentials 인스턴스
//    private final GoogleCredentials credentials;
//
//    public GoogleVisionOcrService() throws IOException {
//        this.credentials = GoogleCredentials.fromStream(
//                GoogleVisionOcrService.class.getClassLoader().getResourceAsStream("combus-413101-2872fecd0b70.json")
//        );
//    }
//
//    // 이미지 파일을 로컬에 저장
//    public String saveFile(MultipartFile file) throws IOException {
//        if (file.isEmpty())
//            return null;
//
//        String originalName = file.getOriginalFilename(); // 파일 원래 이름
//        String uuid = UUID.randomUUID().toString(); // 파일 식별자
//        String extension = originalName.substring(originalName.lastIndexOf(".")); // 파일 확장자 추출
//        String savedName = uuid + extension; // 이미지 파일의 새로운 이름
//        String savedPath = DIR_PATH + savedName; // 파일 경로
//
//        file.transferTo(new File(savedPath)); // local에 파일 저장
//
//        return savedPath;
//    }
//
//    // 이미지 파일로부터 텍스트 추출 및 숫자만 추출
//    public String extractTextAndNumbersFromImage(String imagePath) throws IOException {
//        // 이미지에서 텍스트 추출
//        String extractedText = extractTextFromImage(imagePath);
//
//        // 추출된 텍스트에서 숫자만 추출하여 반환
//        return extractMostFrequentNumber(extractedText);
//    }
//
//    // 이미지 파일로부터 텍스트 추출
//    public String extractTextFromImage(String imagePath) throws IOException {
//        List<AnnotateImageRequest> requests = new ArrayList<>();
//        ByteString imgBytes = ByteString.copyFrom(Files.readAllBytes(Paths.get(imagePath)));
//
//        Image img = Image.newBuilder().setContent(imgBytes).build();
//        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
//        requests.add(request);
//
//        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder()
//                .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build())) {
//            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
//            List<AnnotateImageResponse> responses = response.getResponsesList();
//
//            StringBuilder result = new StringBuilder();
//            for (AnnotateImageResponse res : responses) {
//                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                    result.append(annotation.getDescription()).append("\n"); // 각 행은 새 줄로 구분
//                }
//            }
//            return result.toString();
//        }
//    }
//
//    // 추출된 텍스트에서 가장 많이 나타나는 숫자를 찾아서 반환
//    public String extractMostFrequentNumber(String text) {
//        // 숫자만 추출
//        List<Integer> numbers = extractNumbersFromString(text);
//
//        // 숫자의 빈도를 저장할 맵 생성
//        Map<Integer, Integer> frequencyMap = new HashMap<>();
//        for (int num : numbers) {
//            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
//        }
//
//        // 빈도가 가장 높은 숫자를 찾음
//        int mostFrequentNumber = 0;
//        int maxFrequency = 0;
//        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
//            if (entry.getValue() > maxFrequency) {
//                mostFrequentNumber = entry.getKey();
//                maxFrequency = entry.getValue();
//            }
//        }
//
//        return String.valueOf(mostFrequentNumber);
//    }
//
//    // 문자열에서 숫자만 추출하여 리스트로 반환
//    public List<Integer> extractNumbersFromString(String text) {
//        List<Integer> numbers = new ArrayList<>();
//        // 정규표현식을 사용하여 문자열에서 숫자만 추출
//        Pattern pattern = Pattern.compile("\\d+");
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            numbers.add(Integer.parseInt(matcher.group()));
//        }
//        return numbers;
//    }
//}