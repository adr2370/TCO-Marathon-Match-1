//java -jar BlackAndWhiteVis.jar -exec "<command>"



import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.swing.*;
import javax.imageio.*;
 
public class BlackAndWhiteVis {
    // tuning constants
    final int MIN_SZ = 20, MAX_SZ = 100;
    final double MIN_WHITE_SHARE = 0.05, MAX_WHITE_SHARE = 0.4;
    final double MIN_POWER = 1.25, MAX_POWER = 1.75;

    final char BLACK='.';
    final char WHITE='X';
    final char VISITED='+';
    final char MAX='*';

    final int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};

    // ----------------------------------------------------------------------------------
    int SZ;                   // size of the grid
    volatile char[][] grid;            // the grid itself
    volatile char current_tile;        // the tile in hand used during shifts
    volatile int row, col;             // the cell where the shift applies
    volatile int shift_index;          // the index of the current shift
    ArrayList<Integer> results
    // ----------------------------------------------------------------------------------
    boolean isConnected() {
        // check whether the current state of the grid has all 'X's connected
        // (both for generation and for validation of the return)
        char[][] g = new char[SZ][SZ];
        for (int i = 0; i < SZ; ++i) {
            g[i] = Arrays.copyOf(grid[i], SZ);
        }
        // find the first X, BFS from it and see whether there are any Xs left
        boolean marked_connected = false;
        for (int r0 = 0; r0 < SZ; ++r0)
        for (int c0 = 0; c0 < SZ; ++c0) {
            if (g[r0][c0] == 'X') {
                if (marked_connected) {
                    return false;     // already marked the connected component => something is not connected to it
                }
                // start BFS from this point
                int[] rs = new int[SZ * SZ], cs = new int[SZ * SZ];
                int ns = 1;
                rs[0] = r0;
                cs[0] = c0;
                g[r0][c0] = '+';
                for (int i = 0; i < ns; ++i) {
                    for (int k = 0; k < 4; ++k) {
                        if (rs[i] + dr[k] >= 0 && rs[i] + dr[k] < SZ && 
                            cs[i] + dc[k] >= 0 && cs[i] + dc[k] < SZ && 
                            g[rs[i] + dr[k]][cs[i] + dc[k]] == 'X') {
                            g[rs[i] + dr[k]][cs[i] + dc[k]] = '+';
                            rs[ns] = rs[i] + dr[k];
                            cs[ns] = cs[i] + dc[k];
                            ++ns;
                        }
                    }
                }
                marked_connected = true;
            }
        }
        return true;
    }
    // ----------------------------------------------------------------------------------
    void apply_shift(int dir, int pos) {
        char tmp;
        if (dir == 0) {       // shift pos-th row to the right
            tmp = grid[pos][SZ - 1];
            for (int i = SZ - 1; i > 0; --i) {
                grid[pos][i] = grid[pos][i - 1];
            }
            grid[pos][0] = current_tile;
            current_tile = tmp;
        }
        if (dir == 1) {       // shift pos-th row to the left
            tmp = grid[pos][0];
            for (int i = 0; i < SZ - 1; ++i) {
                grid[pos][i] = grid[pos][i + 1];
            }
            grid[pos][SZ - 1] = current_tile;
            current_tile = tmp;
        }
        if (dir == 2) {       // shift pos-th column down
            tmp = grid[SZ - 1][pos];
            for (int i = SZ - 1; i > 0; --i) {
                grid[i][pos] = grid[i - 1][pos];
            }
            grid[0][pos] = current_tile;
            current_tile = tmp;
        }
        if (dir == 3) {       // shift pos-th column up
            tmp = grid[0][pos];
            for (int i = 0; i < SZ - 1; ++i) {
                grid[i][pos] = grid[i + 1][pos];
            }
            grid[SZ - 1][pos] = current_tile;
            current_tile = tmp;
        }
    }
    // ----------------------------------------------------------------------------------
    boolean apply_shift_returned() {
        // convert the shift in format of return into internal format and apply it
        // return true if this shift is valid
        if (col == -1 && row > -1 && row < SZ) {
            apply_shift(0, row);
            col = SZ;
            return true;
        }
        if (col == SZ && row > -1 && row < SZ) {
            apply_shift(1, row);
            col = -1;
            return true;
        }
        if (row == -1 && col > -1 && col < SZ) {
            apply_shift(2, col);
            row = SZ;
            return true;
        }
        if (row == SZ && col > -1 && col < SZ) {
            apply_shift(3, col);
            row = -1;
            return true;
        }
        return false;
    }

    //finds the largest component and stores it in g[][]
    //returns the dimensions of the largest component
    char[][] findLargestComponent()
    {
      char[][] g=new char[SZ][SZ];
      for (int i=0; i<SZ; i++) for (int k=0; k<SZ; k++) g[i][k]=grid[i][k];
      int maxCount=0;
      int[] rs = new int[SZ * SZ], cs = new int[SZ * SZ];

      // find the first X, BFS from it and see whether there are any Xs left
      for (int r0 = 0; r0 < SZ; ++r0)
      {
        for (int c0 = 0; c0 < SZ; ++c0)
        {
          if (g[r0][c0] != WHITE) continue;

          // start BFS from this point
          int ns = 1;
          rs[0] = r0;
          cs[0] = c0;
          g[r0][c0] = VISITED;
          for (int i = 0; i < ns; ++i)
          {
            for (int k = 0; k < 4; ++k)
            {
              int rNew=rs[i] + dr[k];
              int cNew=cs[i] + dc[k];
              if ( rNew<0 || rNew>=SZ || cNew<0 || cNew>=SZ || g[rNew][cNew] != WHITE) continue;

              g[rNew][cNew] = VISITED;
              rs[ns] = rNew;
              cs[ns] = cNew;
              ++ns;
            }
          }

          if (ns>maxCount)
          {
            maxCount=ns;
            
            //remove previous max component
            for (int i=0; i<SZ; i++) for (int k=0; k<SZ; k++) if (g[i][k]==MAX) g[i][k]=VISITED;
            
            //set this as the max component and find it's dimensions
            for (int i=0; i<ns; i++) g[rs[i]][cs[i]]=MAX;
          }
        }
      }

      return g;
    } 

    // ----------------------------------------------------------------------------------
    void generate(String seed) {
      try {
        int i, j;
        SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
        rnd.setSeed(Long.parseLong(seed));
        if (seed.equals("1")) {
            SZ = MIN_SZ;
        } else {
            SZ = rnd.nextInt(MAX_SZ - MIN_SZ + 1) + MIN_SZ;
        }
        if (debug) {
            System.out.println("Grid size = " + SZ);
        }
 
        // initially the tile in hand is black
        current_tile = '.';
 
        // fill the grid with the initial pattern - a rectangle or a stripe
        int shifts_count;
        double white_share;
        do {
            grid = new char[SZ][SZ];
            for (i = 0; i < SZ; ++i) {
                Arrays.fill(grid[i], '.');
            }
            boolean stripe_mode = (rnd.nextInt(3) == 2);
            while (true) {
                // generate a potential pattern
                int min_row = rnd.nextInt(SZ), max_row = rnd.nextInt(SZ - min_row) + min_row;
                int min_col = rnd.nextInt(SZ), max_col = rnd.nextInt(SZ - min_col) + min_col;
                if (stripe_mode) {
                    if (rnd.nextInt(2) == 1) {
                        min_row = 0;
                        max_row = SZ - 1;
                    } else {
                        min_col = 0;
                        max_col = SZ - 1;
                    }
                }
                // check that it has a proper share of white
                white_share = (max_row - min_row + 1) * (max_col - min_col + 1) * 1.0 / SZ / SZ;
                if (white_share < MIN_WHITE_SHARE || white_share > MAX_WHITE_SHARE) {
                    continue;
                }
                // draw the pattern
                for (i = min_row; i <= max_row; ++i) {
                    for (j = min_col; j <= max_col; ++j) {
                        grid[i][j] = 'X';
                    }
                }
                break;
            }
 
            // apply random shifts without adding current tile to the board
            shifts_count = (int) Math.pow(SZ, rnd.nextDouble() * (MAX_POWER - MIN_POWER) + MIN_POWER);
            //shifts_count=1;     //HACK!
            int direction, position, white_shifted, black_shifted;
            for (i = 0; i < shifts_count || current_tile == 'X'; ++i) {
                // every shift must change the board, i.e., move at least one black and at least one white
                do {
                    direction = rnd.nextInt(4);
                    position = rnd.nextInt(SZ);
                    white_shifted = 0;
                    black_shifted = 0;
                    for (j = 0; j < SZ; ++j) {
                        if (direction < 2 && grid[position][j] == 'X' || direction > 1 && grid[j][position] == 'X') {
                            ++white_shifted;
                        } else {
                            ++black_shifted;
                        }
                    }
                } while (white_shifted == 0 || black_shifted == 0);
                apply_shift(direction, position);
            }
        } while (isConnected());                 // if the result is connected, restart generation
 
        if (debug) {
            System.out.println("% of white = " + white_share);
            System.out.println("Shifts done = " + shifts_count);
            for (i = 0; i < SZ; ++i) {
                System.out.println(new String(grid[i]));
            }
        }
      }
      catch (Exception e) { 
        System.err.println("An exception occurred while generating the test case.");
        e.printStackTrace(); 
      }
    }
    // ----------------------------------------------------------------------------------
    public double runTest(String seed) {
      try {
        int i, j;
        generate(seed);
 
        if (vis)
        {   jf.setSize((SZ + 2) * side + 150, (SZ + 2) * side + 38);
            jf.setVisible(true);
            draw(1);
        }
 
      if (!manual) {
        String gridin[] = new String[SZ];
        for (i = 0; i < SZ; ++i)
            gridin[i] = new String(grid[i]);
        String[] shifts = makeConnected(gridin);
 
        // check the return for validity and apply the shifts from the return simultaneously
        if (shifts == null || shifts.length == 0) {
            addFatalError("Your return must contain at least one element.");
            return 0;
        }
 
        for (shift_index = 0; shift_index < shifts.length; ++shift_index) {
            String[] s = shifts[shift_index].split(" ");
            if (s.length != 2) {
                addFatalError("Each element of your return must be formatted as \"row col\".");
                return 0;
            }
            try {
                row = Integer.parseInt(s[0]);
                col = Integer.parseInt(s[1]);
            } catch (Exception e) {
                addFatalError("Each element of your return must be formatted as \"row col\".");
                return 0;
            }
            draw(2);
            if (!apply_shift_returned()) {
                addFatalError("Element " + i + " of your return specifies an invalid shift. "+row+" "+col);
                return 0;
            }
            draw(2);
            draw(1);
        }
      } else {
        shift_index = -1;
        ready = false;
        while (!ready)
            try { Thread.sleep(1000); }
            catch (Exception e) { };
        manual = false;
      }
 
        // now all shifts are done - check that the result is valid
        if (current_tile != '.') {
            addFatalError("After all shifts all white tiles must stay on the board.");
            return 0;
        }
        if (!isConnected()) {
            addFatalError("After all shifts all white tiles must be connected.");
            return 0;
        }
 
 
        // score is based on the number of shifts performed
        return Math.max(0, 1 - shift_index * 1.0 / SZ / SZ);
      }
      catch (Exception e) { 
        System.err.println("An exception occurred while trying to get your program's results.");
        e.printStackTrace(); 
        return 0.0;
      }
    }
// ------------- server part ------------------------------------------------------------
    public String checkData(String test) {
        return "";
    }
    // ----------------------------------------------------------------------------------
    public String displayTestCase(String test) {
        StringBuffer sb = new StringBuffer();
        sb.append("Seed = "+test);
        generate(test);
        sb.append("Grid size = ").append(SZ).append('\n');
        for (int i = 0; i < SZ; ++i) {
            sb.append(new String(grid[i])).append('\n');
        }
        return sb.toString();
    }
    // ----------------------------------------------------------------------------------
    public double[] score(double[][] sc) {
        double[] res = new double[sc.length];
        //absolute - just a sum
        for (int i=0; i<sc.length; i++)
        {   res[i]=0;
            for (int j=0; j<sc[0].length; j++)
                res[i]+=sc[i][j];
        }
        return res;
    }
// ------------- visualization part -----------------------------------------------------
    static int side;
    static String exec;
    static boolean debug, vis, manual, ready, test;
    static Process proc;
    static int del;
    InputStream is;
    OutputStream os;
    BufferedReader br;
    JFrame jf;
    Vis v;
    volatile int phase;
    // 1 - just the current state of the board and the color of the current tile
    // 2 - put the current tile on the cell before shift
    // ----------------------------------------------------------------------------------
    String[] makeConnected(String[] gridin) throws IOException {
        int i,j;
        String[] ret = new String[0];
        if (exec != null)
        {   //imitate passing params
            StringBuffer sb = new StringBuffer();
            sb.append(SZ).append('\n');
            for (i = 0; i < SZ; ++i)
                sb.append(gridin[i]).append('\n');
            os.write(sb.toString().getBytes());
            os.flush();
 
            int Nret = Integer.parseInt(br.readLine());
            ret = new String[Nret];
            for (i = 0; i < Nret; ++i)
                ret[i] = br.readLine();
        }
        return ret;
    }
    // ----------------------------------------------------------------------------------
    void draw(int ph) {
        if (!vis) return;
        phase = ph;
        v.repaint();
        try { Thread.sleep(del); }
        catch (Exception e) { };
    }
    // ----------------------------------------------------------------------------------
    public class Vis extends JPanel implements MouseListener, WindowListener {
        int selRow=-1;
        int selCol=-1;

        public void paint(Graphics gr) {

            char[][] maxComponent=findLargestComponent();
            int i, j=-1;
            BufferedImage bi = new BufferedImage((SZ + 2) * side + 150, (SZ + 2) * side + 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = (Graphics2D)bi.getGraphics();
            // background
            g2.setColor(new Color(0xEEEEEE));
            g2.fillRect(0, 0, (SZ + 2) * side + 150, (SZ + 2) * side + 1);
            g2.setColor(new Color(0xAAAAAA));
            g2.fillRect(0, 0, (SZ + 2) * side, (SZ + 2) * side);
            // lines in grid
            g2.setColor(Color.BLACK);
            for (i = 0; i <= SZ + 2; ++i)
                g2.drawLine(0, i * side, (SZ + 2) * side, i * side);
            for (i = 0; i <= SZ + 2; ++i)
                g2.drawLine(i * side, 0, i * side, (SZ + 2) * side);
 
            // grid tiles
            for (i = 0; i < SZ; ++i)
            for (j = 0; j < SZ; ++j) {
                if (maxComponent[i][j]==MAX)
                  g2.setColor(Color.RED);
                else
                {
                  // colors - just black and white
                  if (grid[i][j] == '.') {
                      g2.setColor(new Color(0x444444));
                  } else {
                      g2.setColor(new Color(0xFFFFFF));
                  }
                }
                g2.fillRect((j + 1) * side + 1, (i + 1) * side + 1, side - 1, side - 1);
            }
 
            // the player's tile
            if (phase == 1)         // draw the tile "in hand"
            {
                j = SZ + 3;
                i = 1;
                g2.setColor(Color.BLACK);
                g2.drawLine(j * side, i * side, (j + 1) * side, i * side);
                g2.drawLine(j * side, (i + 1) * side, (j + 1) * side, (i + 1) * side);
                g2.drawLine(j * side, i * side, j * side, (i + 1) * side);
                g2.drawLine((j + 1) * side, i * side, (j + 1) * side, (i + 1) * side); 
            }
            else if (phase==2)               // draw the tile on the border of the board, cell (row, col)
            {
                j = col + 1;
                i = row + 1;
            }

            if (current_tile == '.') {
                g2.setColor(new Color(0x444444));
            } else {
                g2.setColor(new Color(0xFFFFFF));
            }
            g2.fillRect(j * side + 1, i * side + 1, side - 1, side - 1);
 
            // the number of shifts done so far
            g2.setColor(Color.BLACK);
            char[] c = ("" + (shift_index + 1)).toCharArray();
            g2.setFont(new Font("Arial",Font.BOLD,14));
            g2.drawChars(c,0,c.length, (SZ + 3) * side, 5 * side);

            //draw current location
            c = ("" + selRow+" "+selCol).toCharArray();
            g2.setFont(new Font("Arial",Font.BOLD,14));
            g2.drawChars(c,0,c.length, (SZ + 3) * side, 5 * side+100); 

            gr.drawImage(bi, 0, 0, (SZ + 2) * side + 150, (SZ + 2) * side + 1, null);
        }
        public Vis() {
            addMouseListener(this);
            jf.addWindowListener(this);
        }
        //MouseListener
        public void mouseClicked(MouseEvent e) {

            // convert to args only clicks with valid coordinates
            row = e.getY() / side - 1;
            col = e.getX() / side - 1;
            if (row != -1 && row != SZ && col != -1 && col != SZ)
            {
                selRow=row;
                selCol=col;
                draw(1);
                return;
            }
            if ((row == -1 || row == SZ) && (col == -1 || col == SZ))     //cannot click on corners
                return;

            // for manual play
            //if (!manual) return;
 
            // a valid shift - apply
            ++shift_index;

            if (!apply_shift_returned())
                return;
            draw(1);
 
            // check whether the result is final
            if (isConnected() && current_tile == '.')
                ready = true;
        }
        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
        //WindowListener
        public void windowClosing(WindowEvent e){ 
            if(proc != null)
                try { proc.destroy(); } 
                catch (Exception ex) { ex.printStackTrace(); }
            System.exit(0); 
        }
        public void windowActivated(WindowEvent e) { }
        public void windowDeactivated(WindowEvent e) { }
        public void windowOpened(WindowEvent e) { }
        public void windowClosed(WindowEvent e) { }
        public void windowIconified(WindowEvent e) { }
        public void windowDeiconified(WindowEvent e) { }
    }
    // ----------------------------------------------------------------------------------
    public BlackAndWhiteVis(String seed) {
      try {
        //interface for runTest
        if (vis)
        {   jf = new JFrame();
            v = new Vis();
            jf.getContentPane().add(v);
        }
        if (exec != null) {
            try {
                Runtime rt = Runtime.getRuntime();
                proc = rt.exec(exec);
                os = proc.getOutputStream();
                is = proc.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                new ErrorReader(proc.getErrorStream()).start();
            } catch (Exception e) { e.printStackTrace(); }
        }
        System.out.println("Score = "+runTest(seed));
        if (proc != null)
            try { proc.destroy(); } 
            catch (Exception e) { e.printStackTrace(); }
      }
      catch (Exception e) { e.printStackTrace(); }
    }
    // --------------- TESTING -------------------------------------------------------------------
    public BlackAndWhiteVis() {
      int startingSeed=2;
      int numTests=100;

      double totalScore=0;
      double badReq=0.7;      //anything below this score is considered bad!
      ArrayList<Integer> badSeeds =new ArrayList<Integer>();

      for (int test=1, seed=startingSeed; test<=numTests; test++,seed++)
      {
        try {
          //interface for runTest
          if (vis)
          {   jf = new JFrame();
              v = new Vis();
              jf.getContentPane().add(v);
          }
          if (exec != null) {
              try {
                  Runtime rt = Runtime.getRuntime();
                  proc = rt.exec(exec);
                  os = proc.getOutputStream();
                  is = proc.getInputStream();
                  br = new BufferedReader(new InputStreamReader(is));
                  new ErrorReader(proc.getErrorStream()).start();
              } catch (Exception e) { e.printStackTrace(); }
          }
          System.out.println("------------------------------------------");
          System.out.println("Testing seed "+seed+" test "+test);
          double score=runTest(""+seed);
          totalScore+=score;
          if (score<badReq) badSeeds.add(seed);
          System.out.println("SCORE = "+score);
          System.out.println("TOTAL SCORE = "+totalScore);
          System.out.println("AVERAGE SCORE = "+totalScore/test);
          System.out.println("NUM BAD = "+badSeeds.size());
          if (proc != null)
              try { proc.destroy(); } 
              catch (Exception e) { e.printStackTrace(); }
        }
        catch (Exception e) { e.printStackTrace(); }
      }

      //print bad seeds
      System.out.print("BAD SEEDS (< "+badReq+"):");
      for (Integer a : badSeeds) System.out.print(" "+a);
      System.out.println();
    }
    // ----------------------------------------------------------------------------------
    public static void main(String[] args) {
        boolean testNot=true;
        boolean stop=false;
        int start=1;
        int end=100;
        boolean printTimes=true;
        del = 1;
        String seed = "1";
        vis = false;
        if(!testNot) vis=true;
        manual = false;
        debug = false;
        side = 10;
        int count=0;
        ArrayList<Integer> suckyOnes=new ArrayList<Integer>();
        for(int i=start;i<=end;i++) {
            long startTime=System.nanoTime();
            BlackAndWhiteVis bw = new BlackAndWhiteVis(i+"");
            long totalTime=System.nanoTime()-startTime;
            if(printTimes) System.out.println(i+" "+totalTime/Math.pow(10,9)+" "+results.get(count));
            if(results.get(count)<60) suckyOnes.add(i);
            count++;
        }
        if(stop&&testNot) {
            vis=true;
            debug=true;
            for(int i=start;i<=end;i++) {
                if(suckyOnes.contains(i)) {
                    BlackAndWhiteVis bw = new BlackAndWhiteVis(i+"");
                    System.out.println(i+" "+results.get(count));
                    count++;
                }
            }
        }
        double avg=0.0;
        for(int i=0;i<results.size();i++) avg+=results.get(i);
        System.out.println(avg/results.size()*100);
    }
    // ----------------------------------------------------------------------------------
    void addFatalError(String message) {
        System.out.println(message);
    }
}
 
class ErrorReader extends Thread{
    InputStream error;
    public ErrorReader(InputStream is) {
        error = is;
    }
    public void run() {
        try {
            byte[] ch = new byte[50000];
            int read;
            while ((read = error.read(ch)) > 0)
            {   String s = new String(ch,0,read);
                System.out.print(s);
                System.out.flush();
            }
        } catch(Exception e) { }
    }
}