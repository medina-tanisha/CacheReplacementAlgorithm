package com.tanishamedina;

import java.util.*;
import java.util.LinkedList;

/**
 * Implementation of MIN, LRU, FIFO, and RAND cahce replacement algorithms.
 *
 * Tanisha Medina
 * Final Version 05-03-24
 */
public class CacheReplacementAlgorithms {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //inputs
        System.out.print("Enter page reference pattern length: ");
        int refLength = sc.nextInt();
        System.out.print("Enter number of unique pages: ");
        int uniqueNum = sc.nextInt();
        System.out.print("Enter number of slots: ");
        int slotNum = sc.nextInt();

        //Randomizes the reference string by using the method for such
        String referenceString = randomize(uniqueNum, refLength);

        //Prints reference string like example picture
        String spacedFormat = referenceString.replace("", " ").trim();
        System.out.println("Ref Str: " + spacedFormat);

        //All four cache replacement algorithms created as linkedlists that are keyed into a hash map 
        Map<String, LinkedList<Character>> algorithms = new HashMap<>();
        algorithms.put("FIFO", new LinkedList<>());
        algorithms.put("LRU", new LinkedList<>());
        algorithms.put("MIN", new LinkedList<>());
        algorithms.put("RAND", new LinkedList<>());
        
        //needed for best and worst algorithm 
        double bestRoH = Double.MIN_VALUE;
        double worstRoH = Double.MAX_VALUE;
        String best = "";
        String worst = "";
        
        
        //for loop lets us iterate through the algorithms
        for (String algorithm : algorithms.keySet()) {
            LinkedList<Character> cache = new LinkedList<>(); //cache will be our null list that we will fill
            LinkedList<Character> referenceChars = new LinkedList<>(); //reference characters are stored here used as page reference
            for (char ch : referenceString.toCharArray()) { //where we copy it from reference string
                referenceChars.add(ch);
            }
            int hits = 0;
            int pageFaults = 0;
            for (int i = 0; i < slotNum; i++) { //makes sure we are getting each algorithm for the number of slots chosen by user
                System.out.print(algorithm + "   " + (i + 1) + ": "); //this prints the number of number slots so each algorithm prints the string of
                for (char page : referenceChars) { //we are checking the reference string letters and calling them pages
                    if (cache.contains(page)) {	//If the page is in the cache a hit will be marked
                        hits++;
                        System.out.print("+ ");
                    } else {	//this is the starter method which helps part of FIFO algorithm
                        if (cache.size() < slotNum) {
                            cache.add(page);
                        } else {
                            cache.remove(0); 
                            cache.add(page);
                            pageFaults++;
                        }
                        System.out.print(page + " ");
                    }
                }
                System.out.println();
            }
            System.out.println("---------------------------");
            
            //Calculate hit rate for determining best and worst
            double rateOfHits = (double) hits / refLength;
            
            //Update best and worst algorithms by using the max and min values of algorithms
            if (rateOfHits > bestRoH) { 
                bestRoH = hits;
                best = algorithm;
            }
            if (rateOfHits < worstRoH) {
                worstRoH = hits;
                worst = algorithm;
            }
        }

        //This section is for cache hit rates of each algorithm and each algorithms cache replacement technique
        System.out.println("Cache Hit Rates:");
        for (String algorithm : algorithms.keySet()) {
            LinkedList<Character> cache = new LinkedList<>(Arrays.asList(new Character[slotNum])); //Makes sure we keep the same size of cache when populated
            LinkedList<Character> referenceChars = new LinkedList<>();
            for (char ch : referenceString.toCharArray()) {
                referenceChars.add(ch);
            }
            int hits = 0;
            int pageFaults = 0;
            for (char page : referenceChars) {
                if (cache.contains(page)) {
                    hits++;
                } else {
                    int index = cache.indexOf(null);
                    if (index != -1) {
                        cache.set(index, page);
                    } else {
                        if (algorithm.equals("FIFO")) { //this is FIFO cache replacement where we check to see if we have room in cache to add and we do
                            if (cache.contains(null)) {
                                cache.set(index, page);
                            } else {
                                cache.remove(0); //if we don't have room or cache does not contain page when we missed and that also accounts for page fault
                                cache.add(page); //to resolve we remove the first one out and add the new page following FIFO
                                pageFaults++;
                            }

                        } else if (algorithm.equals("LRU")) { //This is least recently used cache replacement
                             if (cache.contains(page)) {
                                cache.remove((Character) page); // Remove the page from its current position
                            } else {
                                if (cache.size() >= slotNum) {
                                    cache.removeLastOccurrence(page); //Remove the least used page (which is the last page) if cache is full
                                    pageFaults++;
                                }
                            }
                            cache.add(page); //then we can add that page in place at the end
                        
                        } else if (algorithm.equals("MIN")) { //Min cache replacement algorithm
                            int replaceIndex = getMINIndex(cache, referenceChars); //we use this method to 
                            cache.set(replaceIndex, page); //we are setting our cache up by lowest slot number first 
                            pageFaults++; 
                        } else if (algorithm.equals("RAND")) { //Rand cache replacement algorithm
                            Random random = new Random(); 
                            int replaceIndex = random.nextInt(slotNum); //here we are randomly replacing the index of the characters of their size to set the order
                            if (cache.get(replaceIndex) != null) {
                                pageFaults++; //if we have a null index then that's a page fault
                            }
                            cache.set(replaceIndex, page); //afterwards we add the page by index
                        }
                    }
                }
            }
            double rateOfHits = (double) hits / refLength;

            
            System.out.println(algorithm + " : " + hits + " of " + refLength + " = " + String.format("%.2f", rateOfHits)); //Prints cache hit rates for algorithms
        }

        System.out.println();
        System.out.println("Best: " + best);
        System.out.println("Worst: " + worst);


    }
    // Method to generate reference string
    private static String randomize(int uniqueNum, int refLength) { //method for reference string
        StringBuilder referenceString = new StringBuilder(); //using a string builder to set up the string
        Random rand = new Random();
        for (int i = 0; i < refLength; i++) { //for loop populates reference string with the random characters as a capital letter to the user specified pattern length
            char page = (char) ('A' + rand.nextInt(uniqueNum)); //we make sure that we have the number of unique pages the user asked for
            referenceString.append(page); 
        }
        return referenceString.toString();
    }

    //Method made for MIN algorithm
    private static int getMINIndex(List<Character> cache, List<Character> referenceChars) {
        int minIndex = Integer.MAX_VALUE;
        char minPage = 0;
        for (char ch : cache) { //searching through cache
            int nextOccurrence = referenceChars.indexOf(ch); //setting nexOccurrence equal to the reference string letters
            if (nextOccurrence < minIndex) { //finding out lowest character index
                minIndex = nextOccurrence; //setting it to our new low to find lower if possible
                minPage = ch; //then we set our lowest of all
            }
        }
        return cache.indexOf(minPage); //returns the index of that character from the cache list
    }
}

