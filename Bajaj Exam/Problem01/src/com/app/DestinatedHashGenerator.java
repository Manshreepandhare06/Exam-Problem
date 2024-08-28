package com.app;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.security.MessageDigest;
	import java.security.NoSuchAlgorithmException;
	import java.util.Random;
	import java.util.Scanner;

	import org.json.JSONObject;

	public class DestinationHashGenerator {

	    public static void main(String[] args) {
	        if (args.length != 2) {
	            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to JSON file>");
	            System.exit(1);
	        }

	        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
	        String jsonFilePath = args[1];

	        String destinationValue = getDestinationValue(jsonFilePath);
	        String randomString = generateRandomString(8);
	        String hash = generateHash(prnNumber, destinationValue, randomString);

	        System.out.println(hash + ";" + randomString);
	    }

	    private static String getDestinationValue(String jsonFilePath) {
	        try {
	            File jsonFile = new File(jsonFilePath);
	            Scanner scanner = new Scanner(jsonFile);
	            String jsonString = scanner.useDelimiter("\\Z").next();
	            scanner.close();

	            JSONObject jsonObject = new JSONObject(jsonString);
	            return traverseJSONObject(jsonObject);
	        } catch (FileNotFoundException e) {
	            System.out.println("Error: JSON file not found.");
	            System.exit(1);
	        }
	        return null;
	    }

	    private static String traverseJSONObject(JSONObject jsonObject) {
	        for (String key : jsonObject.keySet()) {
	            if (key.equals("destination")) {
	                return jsonObject.getString(key);
	            } else if (jsonObject.get(key) instanceof JSONObject) {
	                String destinationValue = traverseJSONObject((JSONObject) jsonObject.get(key));
	                if (destinationValue != null) {
	                    return destinationValue;
	                }
	            } else if (jsonObject.get(key) instanceof JSONArray) {
	                JSONArray jsonArray = (JSONArray) jsonObject.get(key);
	                for (int i = 0; i < jsonArray.length(); i++) {
	                    if (jsonArray.get(i) instanceof JSONObject) {
	                        String destinationValue = traverseJSONObject((JSONObject) jsonArray.get(i));
	                        if (destinationValue != null) {
	                            return destinationValue;
	                        }
	                    }
	                }
	            }
	        }
	        return null;
	    }

	    private static String generateRandomString(int length) {
	        Random random = new Random();
	        StringBuilder sb = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            int c = random.nextInt(62);
	            if (c <= 9) {
	                sb.append((char) ('0' + c));
	            } else if (c <= 35) {
	                sb.append((char) ('A' + c - 10));
	            } else {
	                sb.append((char) ('a' + c - 36));
	            }
	        }
	        return sb.toString();
	    }

	    private static String generateHash(String prnNumber, String destinationValue, String randomString) {
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            byte[] bytes = (prnNumber + destinationValue + randomString).getBytes();
	            byte[] digest = md.digest(bytes);
	            StringBuilder sb = new StringBuilder();
	            for (byte b : digest) {
	                sb.append(String.format("%02x", b));
	            }
	            return sb.toString();
	        } catch (NoSuchAlgorithmException e) {
	            System.out.println("Error: MD5 algorithm not found.");
	            System.exit(1);
	        }
	        return null;
	    }
	}

}
