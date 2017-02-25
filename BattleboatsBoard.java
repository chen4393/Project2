import java.util.Scanner;

public class BattleboatsBoard {
	int h;	// height
	int w;	// width
	int[][] board;
	boolean[][] isVisited;
	int numBoats = 0;	// total number of boats
	int numTurns = 0;	// total number of rounds played
	int numSunk = 0;	// total number of sunk boat
	int boatIndex = 0;	// increment by one when new boat added
	int numCannonShots = 0;	// total number of cannon shot
	// print the totally visible board before each turn
	public void display() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++)
				System.out.print(board[i][j] + "\t");
			System.out.println();
		}
	}
	// print the partially visible board before each turn
	public void partialDisplay() {
		//System.out.println(prevResult);
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (isVisited[i][j]) {	// for visited cells
					if (board[i][j] < 0) {	// penalty, hit or sunk
						System.out.print(-1*board[i][j] + "\t");
					} else {	// miss
						System.out.print(board[i][j] + "\t");
					}
				} else {
					System.out.print("X" + "\t");	// for unvisited cells
				}
			}
			System.out.println();
		}
	}
	public BattleboatsBoard(int row, int col) {
		h = row;
		w = col;
		board = new int[h][w];
		isVisited = new boolean[h][w];
		if (w == 3 || h == 3)
			numBoats = 1;
		else if ((w > 3 && w <= 5) || (h > 3 && h <= 5))
			numBoats = 2;
		else if ((w > 5 && w <= 7) || (h > 5 && h <= 7))
			numBoats = 3;
		else if ((w > 7 && w <= 9) || (h > 7 && h <= 9))
			numBoats = 4;
		else if ((w > 9 && w <= 12) || (h > 9 && h <= 12))
			numBoats = 6;
		//System.out.println("numBoats: " + numBoats);
		for (int i = 0; i < numBoats; i++)
			placeBoat();	// set up the game by placing each boat
	}
	public boolean placeBoat() {
		int[] tryBoard = new int[6];	// temporary location array, 3 pairs of coordinates
		boatIndex++;	// increment by one when new boat added
		int attempts = 0;	// make sure not falling into infinite loop
		boolean success = false;	// break the loop early
		boolean isVertical = (Math.random() < 0.5);	// vertical or horizontal
		//System.out.println("isVertical = " + isVertical);
		while (!success && attempts++ < 100) {
			int r = (int)Math.floor(Math.random() * h);	// random row index generated
			int c = (int)Math.floor(Math.random() * w);	// random column index generated
			int iTemp = 0;	// index of the temporary location array tryBoard
			success = true;
			// try to build a solution one step at a time
			// rebuild if fail
			while (success && iTemp < tryBoard.length) {
				// bound checking first!
				if (r >= h || c >= w) {	//bottom fail or right boundary fail
					success = false;
				} else if (board[r][c] == 0) {	//unused cell
					tryBoard[iTemp] = r;
					tryBoard[iTemp+1] = c;
					if(isVertical)
						r++;
					else
						c++;
				} else {
					success = false;	// used cell location
				}
				iTemp += 2;	// go to next pair
			}
		}
		if (!success) {
			// 100 attempts failed, which should not happen
			System.out.println("Failed to placeBoat, attempts: " + attempts);
		}
		// copy the successful coordinates to the board
		for (int i = 0; i < tryBoard.length; i += 2) {
			int row = tryBoard[i];
			int col = tryBoard[i+1];
			board[row][col] = boatIndex;
			//System.out.println("(" + row + ", " + col + ")");
		}
		return success;
	}
	public void play() {
		int turn = 0;
		String result = "";
		int r = 0, c = 0;
		while (numSunk != numBoats) {
			turn++;
			partialDisplay();
			//System.out.println();
			//display();
			System.out.println("Turn " + turn + ":\nPlease enter coordinate (a, b)");
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter a row number: ");
			try {
				r = sc.nextInt();
			} catch (Exception e) {
				System.err.println("Invalid input, please enter an Integer");
				continue;
			}
			System.out.print("Please enter a column number: ");
			try {
				c = sc.nextInt();
			} catch (Exception e) {
				System.err.println("Invalid input, please enter an Integer");
				continue;
			}
			System.out.println(r + ", " + c);
			if (r < 0 || r >= h || c < 0 || c >= w) {
				result = "Penalty";
			} else {
				if (board[r][c] > 0) {
					if (checkSunk(board[r][c])) {
						result = "sunk";
						numSunk++;
					} else {
						result = "hit";
					}
					board[r][c] *= -1;
					numCannonShots++;
				} else if (board[r][c] < 0) {
					result = "Penalty";
				} else {
					result = "miss";
				}
				isVisited[r][c] = true;
			}
			System.out.println(result);
			numTurns++;
		}
		partialDisplay();
		System.out.println("The total number of turns is " + numTurns);
		System.out.println("The total number of cannon shots is " + numCannonShots);
	}
	// check if the hit is the last hit
	public boolean checkSunk(int target) {
		int counter = 0;
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
				if (board[i][j] == target)
					counter++;
		return counter == 1;	// check if the cell is the last part of the boat
	}
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: java BattleboatsBoard numOfRows numOfColumns");
			return;
		}
		int row = Integer.parseInt(args[0]);
		int col = Integer.parseInt(args[1]);
		if (row < 3 || col < 3 || row > 12 || col > 12) {
			System.out.println("Invalid input!\n" +
				"3 <= numOfRows <= 12, 3 <= numOfColumns <= 12");
			return;
		}
		//System.out.println(row + ", " + col);
		BattleboatsBoard bb = new BattleboatsBoard(row, col);
		bb.play();
	}
}