//-----------------------------------------------------------------------------
//Akul Gupta, ID:1686664
// Point.java
// Helper class to represent queen positions on the boolean chessboard
//-----------------------------------------------------------------------------
class Point implements Comparable<Point>{
                int x;
                int y;

                public Point(int c, int r) {
                        x = c;
                        y = r;
                }

                // returns c, r
                public String toString(){
                        return (x+" "+y);
                }

                @Override
                public int compareTo(Point p) {
                        // TODO Auto-generated method stub
                        return this.x - p.x;
                }
                
                public boolean equals(Point p){
                	if(this.x ==p.x&&this.y==p.y){
                		return true;
                	}else{
                		return false;
                	}
                }
        }

