package DataTypes;

public class Constants
{
    public class Boolean
    {
        public static final String YES = "yes";
        public static final String NO = "no";

    }

    public class Protocol {
        public static final String LIGHTCHAIN = "lightchain";
    }

    public class SimulationType
    {
        public static final String STATIC = "static";
        public static final String DYNAMIC = "dynamic";
        public static final String LANDMARK = "landmark";
        public static final String BLOCKCHAIN = "blockchain";
    }

    public class Topology
    {
        public static final boolean GENERATE = true;
        public static final boolean LOAD = false;

        public class GENERATION_TYPE
        {
            public static final String LANADMARK = "landmark";
            public static final String UNIFORM = "uniform";
        }
    }

    public class Churn
    {
        public static final boolean ONLINE = true;
        public static final boolean OFFLINE = false;
        public static final String ARRIVAL = "Arrival";
        public static final String DEPARTURE = "Departure";
        public static final String TYPE = "churn.type";
        public static final String PREDICTOR = "churn.predictor.name";

        public class Type
        {
            public static final String COOPERATIVE = "cooperative";
            public static final String ADVERSARIAL = "adversarial";
        }

        public class Model
        {
            public class Debian
            {
                public class Slow
                {
                    public class SessionLength
                    {
                        public static final double Shape = 0.38;
                        public static final double Scale = 0.706;
                    }

                    public class SessionInterarrival
                    {
                        public static final double Shape = 0.79;
                        public static final double Scale = 0.0025;
                    }
                }

                public class Fast
                {
                    public class SessionLength
                    {
                        public static final double Shape = 0.38;
                        public static final double Scale = 0.706;
                    }

                    public class SessionInterarrival
                    {
                        public static final double Shape = 0.79;
                        public static final double Scale = 0.0003;
                    }
                }

            }

            public class Flatout
            {
                public class SessionLength
                {
                    public static final double Shape = 0.59;
                    public static final double Scale = 0.69;
                }

                public class SessionInterarrival
                {
                    public static final double Shape = 0.79;
                    public static final double Scale = 0.0025;
                }
            }
        }

        public class Parameters
        {
            public static final String BUCKET_SIZE = "bucket.size";
            public static final String CHURN_STABILIZATION = "churn.stab.name";
            public static final String CHURN_STABILIZATION_PARAMETER = "churn.stab.parameter";
            public static final String PREDICTION_PARAMETER = "churn.predictor.parameter";
        }

        public class ChurnStabilizationAlgorithm
        {
            public static final String KADEMLIA = "kademlia";
            public static final String INTERLLACED = "interlace";
            public static final String Tornado = "tornado";
            public static final String DKS = "dks";
            public static final String NONE = "none";
        }

        public class AvailabilityPredictorAlgorithm
        {
            public static final String DBG = "dbg";
            public static final String SWDBG = "swdbg";
            public static final String LIFETIME = "lifetime";
            public static final String LUDP = "ludp";
            public static final String NONE = "none";
        }
    }

    public class SkipGraphOperation
    {
        public class Inserstion
        {
            public static final int EMPTY_LOOKUP_TABLE = 0;
            public static final int NON_EMPTY_LOOKUP_TABLE = 1;
            public static final int NO_INTRODUCER_FOUND = 2;
        }

        public static final int STATIC_SIMULATION_TIME = 0;
    }

    public class Replication
    {

        public static final String FPTI = "replication.fpti";
        public static final String DATA_OWNER_NUMBER = "replication.dataownersnumber";
        public static final String REPLICATION_TIME = "replication.time";

        public class Type
        {
            public static final String PUBLIC = "public";
            public static final String PRIVATE = "private";
        }

        public class Algorithms
        {
            public static final String NONE = "none";
            public static final String RANDOMIZED = "randomized";
            public static final String POWER_OF_CHOICE = "power_of_choice";
            public static final String GLARAS = "glaras";
            public static final String LARAS = "laras";
            public static final String PYRAMID = "pyramid";
            public static final String CLUSTER = "cluster";
            public static final String CORRELATION = "correlation";
        }
    }

    public class NameID
    {
        //Todo add all name ID algorithms
        public static final String DPAD = "dpad";
        public static final String LANS = "lans";
        public static final String LAND = "land";
        public static final String LDHT = "ldht";
        public static final String HIREARCHIAL = "hirearchical";
        public static final String DPLMDS = "dplmds";
        public static final String LMDS = "lmds";
    }

    public class Aggregation
    {
        public class Algorithms
        {
            //Todo add all aggragation names
            public static final String NONE = "none";
        }
    }
}

