import numpy as np
from collections import OrderedDict
import csv

class fpTreeNode:
    def __init__(self, name, freq, parent):
        self.name = name 
        self.freq = freq
        self.parent = parent 
        self.child = OrderedDict() 
        self.link = None
       
    def display_tree_list(self):
        print(self.name, self.freq,end='')
        if len(self.child)>0:
            print(",[",end='')
        for c in self.child.values():
            print("[",end='')
            # rekurzivno pozivamo funkciju za svako dete čvora
            c.display_tree_list()
            if len(c.child)==0:
                print("]",end='')
        print("]",end='')


def export_to_file(data):
    with open(output_file_name, "w",  newline='') as f:
        writer = csv.writer(f, delimiter=',')
        for row in data:
            writer.writerows([[row]])

def similar_item_table_update(similar_item, present_node):
    # obilazenje
    while (similar_item.link != None):
        similar_item = similar_item.link
    similar_item.link = present_node


def fp_tree_preprocess(doc_name, threshold):
    data = np.genfromtxt(doc_name, delimiter=file_delimiter, dtype=str)
    item_freq = {}
    # 1. skeniranje podataka
    for (x,y), value in np.ndenumerate(data):
        # Provera da li je stavka null ili ne
        # Ukoliko nije apendujemo je u item_freq dictionary.
        if value != '':
            if value not in item_freq:
                item_freq[value] = 1
            else:
                item_freq[value] += 1
    # Uklanjanje stavki koje su ispod zadatog supporta (potpore)
    item_freq = {k:v for k,v in item_freq.items() if v >= threshold}
    return data, item_freq


def fp_tree_reorder(data, item_freq):
    root = fpTreeNode('Root',1,None)
    #Dictionary stavki-frekvencija sortiran je na osnovu učestalosti.
    #Ako dve stavke imaju istu učestalost, razvrstavaju se po abecedi prema nazivu ključa.
    sorted_item_freq = sorted(item_freq.items(), key=lambda x: (-x[1],x[0]))
    # Tabela za čuvanje čvorova sličnih stavki kreirana sa svim frekventim stavkama
    sorted_keys = []
    similar_item_dict = {}
    for key in sorted_item_freq:
        similar_item_dict[key[0]] = None # početno su sve njene vrednosti postavljene na “None
        sorted_keys.append(key[0]) 
    # 2. skeniranje podataka
    for row in data:
        # Brisanje svih stavki cija frekvenca nije iznad minimalnog supporta
        trans = []
        for col in row:
            if col in item_freq:
                trans.append(col)
        # Soritranje stavki transakcije (u redu) prema frekvenci
        ordered_trans = []
        for item in sorted_keys:
            if item in trans:
                ordered_trans.append(item)
        # Nakon sortiranja, transakcija se salje FP stablu na ažuriranje
        if len(ordered_trans)!= 0:
            fp_tree_create_and_update(root, ordered_trans, similar_item_dict)
    return root, similar_item_dict


def fp_tree_create_and_update(init_node, trans, similar_item_dict):
    # ako je child vec prisutan, inkrementiramo njegov freq
    if trans[0] in init_node.child:
        init_node.child[trans[0]].freq += 1
    # U suprotnom kreiramo novi cvor za child i povezemo ga sa njegovim parentom
    else:
        init_node.child[trans[0]] = fpTreeNode(trans[0], 1, init_node)
        # Za svaki novokreirani cvor,Similar-Item dictionary se ažurira
        if similar_item_dict[trans[0]] == None:
            # Za 1. čvor, menjamo 'None' vrednost sa samim node
            similar_item_dict[trans[0]] = init_node.child[trans[0]]
        else:
            # Obilaženje do poslednjeg sličnog čvora i ažuriranje novog čvora
            similar_item_table_update(similar_item_dict[trans[0]],\
                                      init_node.child[trans[0]])
    # Funkcija se rekurzivno poziva za svaku stavku u transakciji
    if len(trans) > 1:
        fp_tree_create_and_update(init_node.child[trans[0]], trans[1::],\
                                  similar_item_dict)
        
"""Funkcija za stvaranje uslovnog FP-stabla za svaku stavku koja se često javlja
u glavnom FP-stablu. Funkcija radi potpuno slično funkciji fp_tree_create_and_update(),
osim što se ovde dictionary sa sličnim stavkama ne ažurira"""
def conditional_fptree(name,init_node,data):
    if data[0][0] == name:
        # Preskočiti uslovno stablo ako se ne javljaju dodatne frekventne stavke
        if len(data)>1:
            conditional_fptree(name,init_node,data[1::])
    if data[0][0] != name:
        # Ako se stavka pojavljuje kao child, inkrementirati freq
        if data[0][0] in init_node.child:
            init_node.child[data[0][0]].freq += data[0][1]
        # U suprotnom kreirati novi child node i ažurirati njegovu frekvencu
        else:
            init_node.child[data[0][0]] = fpTreeNode(data[0][0],data[0][1],\
                           init_node)
        # Nastaviti rekurzivno kreiraje uslovnog FP stabla za svaku stavku
        if len(data) >1:
            conditional_fptree(name,init_node.child[data[0][0]],data[1::])

"""Kreira conditional FP-Tree za svaku stavku u Similar-Item dicioary."""
def create_leaf_cond_base(similar_item_dict, threshold):
    final_cond_base = []
    # foreach kroz svaki key-value par u Similar-Item dic
    for key,value in similar_item_dict.items():
        final_cond_base_key = []
        condition_base = []
        leaf_item_freq = OrderedDict()
        # Za svaki key, obilaženje kroz sve linkovane čvorove do kraja
        while value != None:
            path = []
            leaf_node = value
            leaf_freq = value.freq
            # Za isti čvot, obilaženje do parent čvora i apendovanje detalja
            while leaf_node.parent != None:
                leaf_details = [leaf_node.name, leaf_freq]
                path.append(leaf_details) # apendovanje name i value
                leaf_node = leaf_node.parent # prelaz na parent tog čvora
            # Insert the whole path to condition_base
            condition_base.insert(0,path)
            # Jednog kada se određeni čvor završi, inkrementiramo vrednost na value.link
            # Zatim može da se obilazi za sledeći istoimeni čvor
            value = value.link
        # item-set dictionary frekventnosti je kreiran za svaki leaf node
        for row in condition_base:
            for col in row:
                if col[0] not in leaf_item_freq:
                    leaf_item_freq[col[0]] = col[1]
                else:
                    leaf_item_freq[col[0]] += col[1]
        #Stavke ispod supporta se brišu pre kreiranje condition_base
        leaf_item_freq = {k:v for k,v in leaf_item_freq.items() \
                          if v >= threshold}
        # Za svaku transakciju u condition_base, smestaju se stavke
        for row in condition_base:
            temp = []
            temp_tree = []
            for col in row:
                if col[0] in leaf_item_freq:
                    temp.append(col[0]) # smestanje samo imena stavke
                    temp_tree.append(col) # smestanje i imena i frekventnosti stavke
            #Sadzi sve frekventne stavke za odredjeni conditional leaf
            final_cond_base.append(temp) 
            final_cond_base_key.append(temp_tree)
        cond_leaf = key
        cond_root = fpTreeNode('Null Set',1,None)
        # Kreiranje conditional tree od conditonal pattern base-a
        for row in final_cond_base_key:
            conditional_fptree(cond_leaf,cond_root,row)
        # Stampanje conditional tree-a u nested list format ako je  height > 1
        if len(cond_root.child) > 1:
            print('\n--------',"Conditional FP-Tree for",cond_leaf,'--------')
            print("[",end='')
            cond_root.display_tree_list()
            print('\n')
    #Brisanje duplih redova sa istim spiskom stavki
    unique_cond_base_set = set(map(tuple,final_cond_base))
    unique_cond_base_list =list(unique_cond_base_set)
    #Sortiranje liste u alfabetskom redosledu
    unique_cond_base_list.sort(key=lambda \
                               unique_cond_base_list:unique_cond_base_list[0])
    unique_cond_base = map(list,unique_cond_base_list)
    export_to_file(unique_cond_base) # Exportovanje u output csv fajl


"""Main program"""
support = 300 # postavljanje vrednosti support(potpore)
file_name = 'F:\groceries.csv' #ulazni fajl
file_delimiter = ','
output_file_name = "F:\output.csv" #izlazni fajl
# Fukncija koja implementira FP Growth algoritam
dataset, freq_items = fp_tree_preprocess(file_name, support)
fptree_root, header_table = fp_tree_reorder(dataset, freq_items)
create_leaf_cond_base(header_table,support)