public class BattleboatsBoard {
	int h;
	int w;
	int[][] board;
	int numBoats = 0;
	int boatIndex = 0;
	public void display() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++)
				System.out.print(board[i][j] + "\t");
			System.out.println();
		}
	}
	public BattleboatsBoard(int row, int col) {
		h = row;
		w = col;
		board = new int[h][w];
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
			placeBoat();
	}
	public boolean placeBoat() {
		int[] tryBoard = new int[6];	// temporary location array
		boatIndex++;
		int attempts = 0;
		boolean success = false;
		boolean isVertical = (Math.random() < 0.5);	// vertical or horizontal
		//System.out.println("isVertical = " + isVertical);
		while (!success && attempts++ < 100) {
			int r = (int)Math.floor(Math.random() * h);	// random row index
			int c = (int)Math.floor(Math.random() * w);	// random column index
			int iTemp = 0;	// index of the temporary location array tryBoard
			success = true;
			// try to build a solution one step at a time
			while (success && iTemp < tryBoard.length) {
				// bound checking first!
				if (r >= h || c >= w) {	//bottom fail or right boundary fail
					success = false;
				} else if (board[r][c] == 0) {
					tryBoard[iTemp] = r;
					tryBoard[iTemp+1] = c;
					if(isVertical)
						r++;
					else
						c++;
				} else {
					success = false;
					//System.out.println("Used cell");
				}
				iTemp += 2;
			}
		}
		if (!success)
			System.out.println("Failed to placeBoat, attempts: " + attempts);
		// copy the successful coordinates to the board
		for (int i = 0; i < tryBoard.length; i += 2) {
			int row = tryBoard[i];
			int col = tryBoard[i+1];
			board[row][col] = boatIndex;
			//System.out.println("(" + row + ", " + col + ")");
		}
		return success;
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
		bb.display();
	}
}