%#This program was completed using pair programming by
%#(akul gupta, agupta60@ucsc.edu) and
%#(himanshu gautam, hgautam@ucsc.edu ).
%#I acknowledge that each partner in a programming pair should "drive"
%#roughly 50% of the time the pair is working together, and at most 25%
%#of an individual's effort for an assignment should be spent working
%#alone. Any work done by a solitary programmer must be reviewed by the
%#partner. The object is to work together, learning from each other, not
%#to divide the work into two pieces with each partner working on a
%#different piece.
%#We are both submitting the same program.

airport( atl, 'Atlanta         ', degmin(  33,39 ), degmin(  84,25 ) ).
airport( bos, 'Boston-Logan    ', degmin(  42,22 ), degmin(  71, 2 ) ).
airport( chi, 'Chicago         ', degmin(  42, 0 ), degmin(  87,53 ) ).
airport( den, 'Denver-Stapleton', degmin(  39,45 ), degmin( 104,52 ) ).
airport( dfw, 'Dallas-Ft.Worth ', degmin(  32,54 ), degmin(  97, 2 ) ).
airport( lax, 'Los Angeles     ', degmin(  33,57 ), degmin( 118,24 ) ).
airport( mia, 'Miami           ', degmin(  25,49 ), degmin(  80,17 ) ).
airport( nyc, 'New York City   ', degmin(  40,46 ), degmin(  73,59 ) ).
airport( sea, 'Seattle-Tacoma  ', degmin(  47,27 ), degmin( 122,17 ) ).
airport( sfo, 'San Francisco   ', degmin(  37,37 ), degmin( 122,23 ) ).
airport( sjc, 'San Jose        ', degmin(  37,22 ), degmin( 121,56 ) ).



flight( bos, nyc, time( 7,30 ) ).
flight( dfw, den, time( 8, 0 ) ).
flight( atl, lax, time( 8,30 ) ).
flight( chi, den, time( 8,45 ) ).
flight( mia, atl, time( 9, 0 ) ).
flight( sfo, lax, time( 9, 0 ) ).
flight( sea, den, time( 10, 0 ) ).
flight( nyc, chi, time( 11, 0 ) ).
flight( sea, lax, time( 11, 0 ) ).
flight( den, dfw, time( 11,15 ) ).
flight( sjc, lax, time( 11,15 ) ).
flight( atl, lax, time( 11,30 ) ).
flight( atl, mia, time( 11,30 ) ).
flight( chi, nyc, time( 12, 0 ) ).
flight( lax, atl, time( 12, 0 ) ).
flight( lax, sfo, time( 12, 0 ) ).
flight( lax, sjc, time( 12, 15 ) ).
flight( nyc, bos, time( 12,15 ) ).
flight( bos, nyc, time( 12,30 ) ).
flight( den, chi, time( 12,30 ) ).
flight( dfw, den, time( 12,30 ) ).
flight( mia, atl, time( 13, 0 ) ).
flight( sjc, lax, time( 13,15 ) ).
flight( lax, sea, time( 13,30 ) ).
flight( chi, den, time( 14, 0 ) ).
flight( lax, nyc, time( 14, 0 ) ).
flight( sfo, lax, time( 14, 0 ) ).
flight( atl, lax, time( 14,30 ) ).
flight( lax, atl, time( 15, 0 ) ).
flight( nyc, chi, time( 15, 0 ) ).
flight( nyc, lax, time( 15, 0 ) ).
flight( den, dfw, time( 15,15 ) ).
flight( lax, sjc, time( 15,30 ) ).
flight( chi, nyc, time( 18, 0 ) ).
flight( lax, atl, time( 18, 0 ) ).
flight( lax, sfo, time( 18, 0 ) ).
flight( nyc, bos, time( 18, 0 ) ).
flight( sfo, lax, time( 18, 0 ) ).
flight( sjc, lax, time( 18,15 ) ).
flight( atl, mia, time( 18,30 ) ).
flight( den, chi, time( 18,30 ) ).
flight( lax, sjc, time( 19,30 ) ).
flight( lax, sfo, time( 20, 0 ) ).
flight( lax, sea, time( 22,30 ) ).


radian(Deg,Ans):- Ans is (Deg * pi/180).


%where X,Y are degmin

%#----------lon1 ----------lat1 -------------lon2 ---------------lat2

distance(degmin(Deg00,Min00),degmin(Deg01,Min01),degmin(Deg10,Min10),degmin(Deg11,Min11), Ans):- degree(Deg00,Min00,Lat1), degree(Deg01,Min01,Lon1),
                                                                                degree(Deg10,Min10,Lat2), degree(Deg11,Min11,Lon2),
																				Dlon is (Lon2-Lon1), Dlat is (Lat2-Lat1), radian(Dlat,Dlat_rad), radian(Lat1,Lat1_rad), radian(Lat2,Lat2_rad), radian(Dlon,Dlon_rad),
																				A is  (sin(Dlat_rad/2)**2) + (cos(Lat1_rad) * cos(Lat2_rad) * (sin(Dlon_rad/2)**2)) ,
																				C is 2 * atan2(sqrt(A), sqrt(1-A) ), R is 3956, Ans is R * C.
																				

degree(Deg,Min,Ans):- Ans is (Deg + 1/60*Min).



delta_time(degmin(Deg00,Min00),degmin(Deg01,Min01),degmin(Deg10,Min10),degmin(Deg11,Min11), Ans):-distance(degmin(Deg00,Min00),degmin(Deg01,Min01),degmin(Deg10,Min10),degmin(Deg11,Min11), D), Ans is D/500.

%# fly(A,B,Ans):-flight(A,X,_), flight(X,B,_), Ans=(A,X,B).

%lastFlight(time(H2,M2),A,X,B):-calcArrival(A,B,time(H1,M1),time(Arrival_H,Arrival_M)),
%                          addThirty(time(Arrival_H,Arrival_M),H_,M_), (H2=:=H_,M2>=M_; H2>H_).


fly(A,B,Ans,Depart):-flight(A,B,T), Depart=T, Ans=[(A,B,T)].
% # -----------------foward checking so no infinite loop----------------------------------|flightAllowed(Temp1, Temp2, X,B),
fly(A,B,Ans,Depart):-flight(A,X,Temp1),flight(X,B,Temp2), flightAllowed(Temp1, Temp2, A,X), fly(A,X,Ans1,Depart1),  fly(X,B,Ans2,Temp2),  Depart = Depart1,!, append(Ans1,Ans2,Ans).

fly(A,B):-flight(A,_,_),flight(_,B,_), fly(A,B,Ans,_),!, printTripList(Ans).
fly(A,B):- flight(A,_,_),flight(_,B,_),fly(A,X,Ans1, Depart1), fly(X,B,Ans2, Depart2),flightAllowed(Depart1, Depart2, A,X),!,  append(Ans1,Ans2,Ans), printTripList(Ans).



%flightAllowed(Depart1, Depart2, A,X)


addThirty(time(H,M), H_, M_):- Added is M + 30, H_ is (H + floor(Added/60)), M_ is mod(Added,60).


calcArrival(A,B,time(Start_H,Start_M),Arrival):- airport(A,_,D1,D2),airport(B,_,D3,D4), delta_time(D1,D2,D3,D4,Delta),
% ------------------------------------------------------------------------------------------------------------------------------------changed this part after
                                    MinDelta is round((Delta - floor(Delta))*60), Added is Start_M + MinDelta, H_ is (Start_H + floor(Delta) + floor(Added/60)), M_ is mod(Added,60), Arrival = time(H_,M_).
									%Hours is floor(Delta), Min is round( mod(Delta,1)*60), HoursP is (Hours + floor((Min+Start_M)/60)),MinP is mod((Min+Start_M),60), End_H is (Start_H + HoursP), End_M is %MinP, Arrival = time(End_H,End_M).

temp(A,B,Delta):-airport(A,_,D1,D2),airport(B,_,D3,D4), delta_time(D1,D2,D3,D4,Delta).

% #if time is less than 24hr and the later flight is after it has landed + 30 min
% #			time arrival-time of second flight, A is start dest, B is end Dest
% # H2 second flight, H_ earliest next flight
flightAllowed(time(H1,M1),time(H2,M2),A,B):- ((H1=:=23, M1=< 59) ; (H1=<23)),
											calcArrival(A,B,time(H1,M1),time(Arrival_H,Arrival_M)), 
											addThirty(time(Arrival_H,Arrival_M),H_,M_), (H2=:=H_,M2>=M_; H2>H_).



print_trip( Action, Code, Name, time( Hour, Minute)) :-
   upcase_atom( Code, Upper_code),
   format( "~6s  ~3s  ~s~26|  ~`0t~d~30|:~`0t~d~33|",
           [Action, Upper_code, Name, Hour, Minute]),
   nl.


printTrip(Start,End,Time) :-airport( Start, Name1,_,_),airport( End, Name2,_,_),
   print_trip( depart, Start, Name1, Time),
   calcArrival(Start,End,Time,Time2), print_trip( arrive, End, Name2, Time2), nl.

printTripList([]).
printTripList([X|Xs]):- (A,B,T) = X, printTrip(A,B,T),printTripList(Xs).


main :- read(A),read(B), fly(A,B).



testDistance(A,B,Ans):-airport( A,_, D1, D2), airport(B,_, D1p, D2p), distance(D1,D2,D1p,D2p, Ans).











