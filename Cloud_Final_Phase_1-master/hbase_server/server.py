import tornado.ioloop
import tornado.web
import tornado.process
import sys
import happybase
import time
import datetime

# global variable
EMR_MASTER_DNS = 'ec2-54-172-135-214.compute-1.amazonaws.com'
TMP_TABLE_NAME = 'user_tb'

X = 6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153

# functions
def open_connection(dns_name):
	connection = happybase.Connection(dns_name)
	connection.open()
	return connection

def print_tables(in_conn):
	print in_conn.tables()

def create_table(in_conn, tb_name):
	in_conn.create_table(
		tb_name,
		{
			'user_tb': dict(),  # use defaults
		}
	)

hb_conn = open_connection(EMR_MASTER_DNS)
my_table = hb_conn.table(TMP_TABLE_NAME)

C = {'14444933782014-04-04 02:57:47':['451916513428385792:0:RT @m2w2m: \xf0\x9f\x94\xb4 \xd9\x84\xd9\x84\xd9\x85\xd8\xaa\xd9\x88\xd8\xa7\xd8\xac\xd8\xaf\xd9\x8a\xd9\x86 \xd9\x84\xd8\xb2\xd9\x8a\xd8\xa7\xd8\xaf\xd9\x87 \xd9\x85\xd8\xaa\xd8\xa7\xd8\xa8\xd8\xb9\xd9\x8a\xd9\x86\xd9\x83;\xe2\x9c\x85\xe2\x99\xa61-\xd8\xaa\xd8\xa7\xd8\xa8\xd8\xb9\xd9\x86\xd9\x8a @m2w2m;\xe2\x9c\x85\xe2\x99\xa62-\xd8\xb1\xd9\x8a\xd8\xaa\xd9\x88\xd9\x8a\xd8\xaa \xd8\xa2\xd9\x84\xd9\x8a http://t.co/OSYApeOn3P;\xe2\x9c\x85\xe2\x99\xa63-\xd8\xaa\xd8\xa7\xd8\xa8\xd8\xb9 \xd9\x85\xd9\x86 \xd9\x8a\xd8\xb3\xd9\x88\xd9\x8a \xd8\xb1\xd8\xaa\xd9\x88\xd9\x8a\xd8\xaa;\xe2\x9c\x85\xe2\x99\xa64-\xd8\xa7\xd9\x84\xd8\xaa\xd8\xb2\xd9\x85 \xd8\xa8\xd8\xa7\xd9\x84\xd8\xb4\xd8\xb1\xd9\x88\xd8\xb7 \xd9\x84\xd8\xaa\xe2\x80\xa6;']}
mapping = {}

class MainHandler(tornado.web.RequestHandler):
	def get(self):
		temp =  self.request.uri.split('=')
		uid  = temp[1].split('&')[0]
		time = temp[2]
		time = time.replace('+',' ')
		start = uid+time

		l = []
		self.write('NoLife,900670946900,846052939032,028020059431\n')
		if start in C:
			l = C[start]
			for i in l:
				self.write(i)
		else:
			for k,v in my_table.scan(row_prefix=start):
				print v
				out = v['user:tID']+':'+v['user:Score']+':'+v['user:Text'].strip().replace('\\n','')+'\n'
				l.append(out)

			C[start] = l
			for i in C[start]:
				self.write(i)

# handler for heartbeat check
class HeartbeatHandler(tornado.web.RequestHandler):
	def get(self):
		XY = self.request.uri.split('=')[1]
		if XY in mapping:
			Y = mapping[XY]
		else:
			Y  = long(XY)/X

		ts = time.time()
		time_stamp = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
		self.write(str(Y) + '\nNoLife,900670946900,846052939032,028020059431\n' + str(time_stamp) + '\n')

class NHandler(tornado.web.RequestHandler):
	def get(self):
        	self.write("")

application = tornado.web.Application([
	(r"/q2?", MainHandler),
	(r"/q1?", HeartbeatHandler),
	(r"/", NHandler),
])

if __name__ == "__main__":
	application.listen(8080)
	print 'listening on 8080'
	tornado.ioloop.IOLoop.instance().start()
	tornado.process.fork_processes(10, 100)
