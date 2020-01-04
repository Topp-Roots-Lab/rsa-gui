class LinkedGraph
{
public:
	LinkedGraph(int nNodes, int maxEdges);		
	~LinkedGraph();	
	int addNode() {return nNodes++;}
	void addEdge(int node1, int node2);
	void deleteEdge(int node1, int node2);
	int degree(int nodeId) {return _degrees[nodeId];}
	int node_num() {return nNodes;}
	int edge_num() {return nEdges;}
	int firstAdjNode(int nodeId);

private:
	void addAdjNode(int node1, int node2, int link);
	void deleteAdjNode(int node1, int node2);

	int* _firstLink;		//	link to first next node (nNode entry)
	int* _adjNodeId;		//	adjacent node id (nEdges*2 entry)
	int* _nextLink;		//	link to next node (nEdges*2 entry)
	int* _degrees;		//	count degree for each node (nNode entry)

	int nNodes;			//	#. nodes
	int nEdges;			//  #. edges
};

LinkedGraph::LinkedGraph(int nNodes, int maxEdges)
{
	this->nNodes = nNodes; 
	this->nEdges = 0;
	_firstLink = new int[nNodes];
	for (int i = 0; i < nNodes; ++i) _firstLink[i] = -1;
	_nextLink = new int[maxEdges*2];
	for (int i = 0; i < maxEdges*2; ++i) _nextLink[i] = -1;
	_degrees = new int[nNodes];
	memset(_degrees, 0, sizeof(int)*nNodes);
	_adjNodeId = new int[maxEdges*2];
}

LinkedGraph::~LinkedGraph()
{
	delete[] _firstLink;
	delete[] _adjNodeId;
	delete[] _nextLink;		
	delete[] _degrees;		
}

void LinkedGraph::addEdge(int node1, int node2)
{
	addAdjNode(node1, node2, nEdges+nEdges);
	addAdjNode(node2, node1, nEdges+nEdges+1);
	++nEdges;
}

void LinkedGraph::addAdjNode(int node1, int node2, int link)
{
	_degrees[node1]++;
	_adjNodeId[link] = node2;
	if (_firstLink[node1] != -1)
		_nextLink[link] = _firstLink[node1];
	_firstLink[node1] = link;
}

void LinkedGraph::deleteEdge(int node1, int node2)
{
	deleteAdjNode(node1, node2);
	deleteAdjNode(node2, node1);
	--nEdges;
}

void LinkedGraph::deleteAdjNode(int node1, int node2)
{
	_degrees[node1]--;
	int index_cur = _firstLink[node1];
	int index_prev = -1;
	while (_adjNodeId[index_cur] != node2) {
		index_prev = index_cur;
		index_cur = _nextLink[index_cur];
	}
	if (index_cur == _firstLink[node1])
		_firstLink[node1] = _nextLink[index_cur];
	else
		_nextLink[index_prev] = _nextLink[index_cur];
}

int LinkedGraph::firstAdjNode(int nodeId)
{
	int index = _firstLink[nodeId];
	if (index == -1) return -1;
	return _adjNodeId[index];
}

//int main()
//{
//	LinkedGraph* mygraph = new LinkedGraph(10, 20);
//	mygraph->addEdge(1,2);
//	mygraph->addEdge(3,4);
//	mygraph->addEdge(1,4);
//	for (int i = 0; i < 10; ++i)
//		cout << mygraph->degree(i) << " ";
//	cout << endl;
//	for (int i = 0; i < 10; ++i)
//		cout << mygraph->firstAdjNode(i) << " ";
//	cout << endl;
//
//	mygraph->deleteEdge(1,2);
//	for (int i = 0; i < 10; ++i)
//		cout << mygraph->degree(i) << " ";
//	cout << endl;
//	for (int i = 0; i < 10; ++i)
//		cout << mygraph->firstAdjNode(i) << " ";
//	cout << endl;
//	return 0;
//}
