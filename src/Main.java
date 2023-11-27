import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}



class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        Map<Integer, List<int[]>> adj = new HashMap<>();
        for (int[] i : flights)
            adj.computeIfAbsent(i[0], value -> new ArrayList<>()).add(new int[] { i[1], i[2] }); // creat arraylist and add  desination and cost all in 1 go

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);

        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[] { src, 0 }); // q will hold location and cost
        int stops = 0;

        while (stops <= k && !q.isEmpty()) {
            int sz = q.size();
            // Iterate on current level.
            while (sz-- > 0) {
                int[] temp = q.poll();
                int node = temp[0];
                int distance = temp[1];

                if (!adj.containsKey(node))
                    continue;
                // Loop over neighbors of popped node.
                for (int[] e : adj.get(node)) {
                    int neighbour = e[0];
                    int price = e[1];
                    if (price + distance >= dist[neighbour])
                        continue;
                    dist[neighbour] = price + distance;
                    q.offer(new int[] { neighbour, dist[neighbour] }); // pass neighborr and new cost
                }
            }
            stops++;
        }
        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}

// my solution passes 40/53 then time limit exceeded. still freaking solid though... estimated
// time to solve was 45 min, on par with a hard
class Solution {
    int cost = Integer.MAX_VALUE;
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        List<List<Integer>> cityAdjacency = new ArrayList<>();
        List<List<Integer>> priceAdjacency = new ArrayList<>();

        for(int i = 0; i < n; i++){ // empty neighboring cities list for each city as its corresponding index
            cityAdjacency.add(new ArrayList<>());
            priceAdjacency.add(new ArrayList<>()); // for each neighboring city that we can fly to, there
            // must be a corresponding value at a matching index
        }
// [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]]
        for(int[] flight: flights){
            cityAdjacency.get(flight[0]).add(flight[1]); // get the neighboring cities list at city index
            priceAdjacency.get(flight[0]).add(flight[2]); // get price arraylist for city at index and add the cost
            // at this point, directed adjacency list and its corresponding prices to each destination is complete
        }

        boolean[] visited = new boolean[n]; // will hold T/F for each city, defaulting to F

        DFS(src,dst,k,visited,0, cityAdjacency, priceAdjacency);

        if (cost < Integer.MAX_VALUE){
            return cost;
        }
        return -1;
    }

    // will need to make sure if desination found, then return max, such that max will never be updated as cost
    private void DFS(int src, int dst, int k, boolean[] visited, int costSoFar,
                    List<List<Integer>> cityAdjacency, List<List<Integer>> priceAdjacency){

        if(k<-1 || visited[src]){  // if we've exceeded allowed stops then return
            return;
        }

        if(src == dst){ // if we are at our desination, don't bother marking as visited, because we will nneed to try
            // to get here via another path anyway
            cost = Math.min(costSoFar,cost); // the updated cost will have been passed once a new city recursively searched
            // so update if less. original cost val set to max so any successful first route will update as new low cost
            return;
        }

        visited[src] = true; // if we get to here, it is a new location within an accetpable distance but is now our
        // final destination

        for(int i = 0; i < cityAdjacency.get(src).size(); i++){ // now DFS through next city and see where it can take us
            int additionalPrice = priceAdjacency.get(src).get(i); // derive the fee for next location
            DFS(cityAdjacency.get(src).get(i),dst,k-1,visited,costSoFar + additionalPrice,cityAdjacency,priceAdjacency);
            // pass the fee as parameter along with the neighbor as the next city and k-1 to indicate # of stops left
            // we are allowed to make
            visited[src] = false; // if we've visited here, then return but mark location as false since it may be
            // used to get to destination later via more efficient path. marking as false if already false is acceptable
        }

    }
}

// n is number of locations where flights.length would be number of flights
// such that more than one flight can leave from a city. obviously since edges
// is effectively > than nodes there will be a cycle, otherwise there would be
// no way to get back to start city
// k is number of stops, effectively # of flights -1. this is layovers
// src is start point, dst is the destination

// format of the flights[][] info will be like this. [start,end,cost]
// will need to build edges and keep track of prior destinations to avoid cycle
// build an adjacency list with non-reciprocal edge but need something extra
// because just storing the neighbor will lose the price data

// store prices separately in an array. take advantage of the cities being labeled 0-(n-1)
// say adjacency list and price arrayList like this
/*

(0, [1])            (0, [100])
(1, [2,3])          (1, [100,600])
(2, [3])            (2, [200])

since we pursue neighboring cities sequentially, we can store the fight cost sequentially
 as well. say flight from city 0 goes to index 0 of its connecting cities and the price
 is 100 at index 0 for coresponding city at index 0. same thing when getting to city 2
 which has two connecting cities. we explore sequentially from 0-(n-1) cities so we
 store city 0 at index 0 and price 100 at index 0 in prices. can't fly to city 1 or 2
 since not in array of city 2 so moving onto city 3, we store city 3 at index 1 and price
 in prices arraylist at index 1. i think the prices arraylist would actually be the
 value in a map so er create the directed adjacency list and then call the city as key
 and search its corresponding prices.

 again would have to have have a boolean[] for visited where say 0 is visited, then
 1 is visited , then 2 is visited (attempting to get to 3) but check 0 first and see its
 already been visited so check next city in 2's adjacency list which is 3. destination
 met so add the costs then backtrack from 3 to 2. but now 2 is through with its adjacency
 cities re unmark boolean[2] = false which brings us back to city 1. city one's for loop
 through its adjacency cities already looped paset city 2 so the fact that is is now
 fallse for visited doesn't matter. we pursue next city of 3 which is destination and
 do math.min of stored flioght value and current flight route value
 adjacency list at city 1 finished as would be city 0 . function completes.

 only thing is that along the way also have to state that if stops has exceeded k then
 backtrack

*/