import BaseHTTPServer, json, sys

NUM_PLAYERS_PER_GAME=int(sys.argv[1]) if len(sys.argv)>1 else 3

peers = {}
told  = set()

class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
	def do_GET(self):
		global peers, told
		
		player_name, peer_port = self.path.split('/')[-2:]
		
		#if player_name in { n for a,p,n in peers } and (self.client_address[0], peer_port, player_name) not in peers:
		if (self.client_address[0], peer_port) not in peers and player_name in peers.values():
			print >> self.wfile, "nickname-present"
			#print "The nickname {} has already been taken. Choose a different nickname.".format(player_name)
			return
		
		if len(peers) < NUM_PLAYERS_PER_GAME:			
			# Add to peer set
			peers[(self.client_address[0], peer_port)] = player_name
			print peers
		
		if len(peers) == NUM_PLAYERS_PER_GAME:
			self.send_response(200)
			self.end_headers()
			print >> self.wfile, "start"
			print >> self.wfile, json.dumps(list(enumerate([ [h,p,n] for (h,p),n in peers.iteritems() ])))
			told |= { (self.client_address[0], peer_port) }
			print told
			print peers
			if told == set(peers.viewkeys()):
				peers = {}
				told = set()
				print '# TOLD EVERYONE. RESETTING FOR A NEW GAME...'
		else:
			self.send_response(200)
			self.end_headers()
			print >> self.wfile, "wait"
			print >> self.wfile, NUM_PLAYERS_PER_GAME-len(peers)

def run(server_class=BaseHTTPServer.HTTPServer,
        handler_class=BaseHTTPServer.BaseHTTPRequestHandler):
    server_address = ('', 8080)
    httpd = server_class(server_address, handler_class)
    print "starting server for %d players..." % NUM_PLAYERS_PER_GAME
    httpd.serve_forever()


run(handler_class=Handler)
