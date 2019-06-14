package mu.mvy.mokachain.node;


public abstract class Config {

    /**
     * Address of a Node to use for initialization
     */
    public static final String MASTER_NODE_ADDRESS = "http://localhost:8080";
    
    public static final int BLOCK_SIZE = 32;
    
    
    /**
     * Address of a master node url
     */
    public static final int MASTER_NODE_SWARM_URL = 0;

    /**
     * Minimum number of leading zeros every block hash has to fulfill
     */
    public static final int DIFFICULTY = 3;

    /**
     * Maximum number of Transactions a Block can hold
     */
    public static final int MAX_TRANSACTIONS_PER_BLOCK = 5;
    
    
    public static final String MOKACHAIN_SECRET_PASS = "SleepingGenieV1.0.0";


}
