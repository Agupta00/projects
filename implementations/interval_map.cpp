#include <map>
#include <limits>
#include <iostream>
#include <assert.h>


using namespace std;

template<typename K, typename V>
class interval_map {
	std::map<K,V> m_map;

public:
    // constructor associates whole range of K with val by inserting (K_min, val)
    // into the map
    interval_map( V const& val) {
        m_map.insert(m_map.end(),std::make_pair(std::numeric_limits<K>::lowest(),val));
    }

    // Assign value val to interval [keyBegin, keyEnd).
    // Overwrite previous values in this interval.
    // Conforming to the C++ Standard Library conventions, the interval
    // includes keyBegin, but excludes keyEnd.
    // If !( keyBegin < keyEnd ), this designates an empty interval,
    // and assign must do nothing.
    void assign( K const& keyBegin, K const& keyEnd, V const& val ) {
        if (!(keyBegin<keyEnd))
            return;

        auto begin=m_map.lower_bound(keyBegin);

        //plug is used to fill extend the interval that [keyBegin, keyEnd) belongs to
        //ie previous map of (0,A), adding in b=[2,4), is (0,A), (2,B), (4, plug), where plug=A
        auto plug=std::prev(begin)->second;
        if(!(begin->first>keyBegin)){
            plug=begin->second;
            begin->second=val;
            begin++;
        }
        else{
            //add the value before where begin points to
            begin= m_map.insert(--begin, std::pair<K,V>(keyBegin,val));
            begin++;
        }

        auto end=m_map.lower_bound(keyEnd);
        //delete old values within the range
        while(begin!=end){
            plug=begin->second;
            begin=m_map.erase(begin);
        }

        //if we need to add the plug to plug-in the interval that [keyBegin, keyEnd) belongs to
        if(( keyEnd<std::numeric_limits<K>::max() &&end==m_map.end()) ||
            (end!=m_map.end()&& keyEnd<end->first)){
            m_map.insert(--begin, std::pair<K,V>(keyEnd,plug));
        }


    }

    // look-up of the value associated with key
    V const& operator[]( K const& key ) const {
        return ( --m_map.upper_bound(key) )->second;
    }
};

void IntervalMapTest()
{
    interval_map<unsigned int,char> test('m');
    test.assign(2,4,'k');
    test.assign(4,5,'s');
    test.assign(5,6,'t');
    test.assign(10,60,'z');
    test.assign(5,6,'r');
    test.assign(0,2,'t');
    test.assign(12,13,'q');
    
    for(int i=0;i<15;i++){
        cout<<"i: "<<i<< "map val "<<test[i]<<endl;
    }
}
int main(int argc, char* argv[]) {
    cout<<"hello";
    IntervalMapTest();
    return 0;
}

// Many solutions we receive are incorrect. Consider using a randomized test
// to discover the cases that your implementation does not handle correctly.
// We recommend to implement a test function that tests the functionality of
// the interval_map, for example using a map of unsigned int intervals to char.