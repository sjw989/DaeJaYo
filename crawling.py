import requests
import urllib
import re
import json, csv
from bs4 import BeautifulSoup as bs

def toJson(recipe_dict):
    with open('recipe.json', 'w', encoding='utf-8') as file :
        json.dump(recipe_dict, file, ensure_ascii=False, indent = '\t')
    

def toCSV(recipe_list):
    with open('ingredients.csv', 'w', encoding='utf-8', newline='') as file :
        csvfile = csv.writer(file)
        for row in recipe_list:
            csvfile.writerow(row)
            
def url_func(n,m):
    num_range = range(n,m)
    
    # url --> 크롤링할 페이지
    url = "https://www.10000recipe.com/recipe/list.html?cat4=53&order=reco&page="
    url_list = []
    
    # 만약에 num_rage = (1,10)이면 1~9페이지까지 크롤링함
    for num in num_range:        
        req = urllib.request.Request(url + str(num))        
        code = urllib.request.urlopen(url + str(num)).read()
        soup = bs(code, "html.parser")        
        try:
            res = soup.find(class_='common_sp_list_ul ea4')            
            for i in res.find_all('a'):
                url_tmp = i.get('href')
                if url_tmp[1] == 'r' :                    
                    url_list.append(url_tmp)
        except(AttributeError):
            pass        
    return url_list    

num_id = 0
food_dicts = []
ingre_set = set()
url_lists = url_func(1,10)
for url_str in url_lists:
    url = "https://www.10000recipe.com"
    url = url + url_str
    req = urllib.request.Request(url)
    code = urllib.request.urlopen(url).read()
    soup = bs(code, "html.parser")
    
    info_dict = {}
    ingre_list = []
    ingre_dict = {}
    recipe_list = []
    recipe_dict = {}
    food_dict = {}
    
    res = soup.find('div','view2_summary')
    res = res.find('h3')
    menu_name = res.get_text()
    
    res = soup.find('div', 'centeredcrop')
    res = res.find('img')
    menu_img = res.get('src')
    
    res = soup.find('div', 'view2_summary_in')
    if(str(type(res)) == "<class 'bs4.element.Tag'>"):
        menu_summary = res.get_text().replace('\n','').strip()    
    else:
        menu_summary = " ";
        
    res = soup.find('span', 'view2_summary_info1')
    if(str(type(res)) == "<class 'bs4.element.Tag'>"):
        menu_info_1 = res.get_text()        
    else:
        menu_info_1 = " " 
    res = soup.find('span', 'view2_summary_info2')
    if(str(type(res)) == "<class 'bs4.element.Tag'>"):    
        menu_info_2 = res.get_text()
    else:
        menu_info_1 = " " 
    res = soup.find('span', 'view2_summary_info3')
    if(str(type(res)) == "<class 'bs4.element.Tag'>"):    
        menu_info_3 = res.get_text()
    else:
        menu_info_1 = " " 
    
    
    info_dict = {"info1":menu_info_1,
                 "info2":menu_info_2,
                 "info3":menu_info_3}    
        
    res = soup.find('div','ready_ingre3')        
    try :
        for n in res.find_all('ul'):
            for tmp in n.find_all('li'):
                ingredient_name = tmp.get_text().replace('\n','').replace(' ','')                
                count = tmp.find('span')
                ingredient_tmp = count.get_text()                
                                
                if ingredient_tmp.size() > 0 and ingredient_tmp[-1] == ')' and ingredient_tmp[-2] == "개":                    
                    ingredient_tmp = ingredient_tmp[:-1] 
                       
                ingredient_name = re.sub(ingredient_tmp,'',ingredient_name)
                ingredient_unit = ingredient_tmp.replace('/','').replace('+','')
                ingredient_unit = ''.join([i for i in ingredient_unit if not i.isdigit()])
                ingredient_count = re.sub(ingredient_unit, '', ingredient_tmp)
                
                ingre_dict = {"ingre_name":ingredient_name,
                              "ingre_count":ingredient_count,
                              "ingre_unit":ingredient_unit,}
                ingre_list.append(ingre_dict)
                
                ingre_set.add(ingredient_name)
    except(AttributeError):
        pass
    
    res = soup.find('div','view_step')        
    for n in res.find_all('div', 'view_step_cont'):
        recipe_stemp_txt = n.get_text().replace('\n',' ')
                
        tmp = n.find('img')            
        if str(type(tmp)) == "<class 'bs4.element.Tag'>":            
            recipe_step_img = tmp.get('src')            
        else:
            recipe_step_img = " "
            
        recipe_dict = {"txt":recipe_stemp_txt,
                       "img":recipe_step_img,}
        recipe_list.append(recipe_dict)    
    
    num_id = num_id + 1
    food_dict = {"id":num_id,
                 "name":menu_name,
                 "img":menu_img,
                 "summary":menu_summary,
                 "info":info_dict,
                 "ingre":ingre_list,
                 "recipe":recipe_list}
    food_dicts.append(food_dict)        

toJson(food_dicts)

ingre_list_csv=[]
for i in ingre_set:
    tmp_l = []
    tmp_l.append(i)
    ingre_list_csv.append(tmp_l)
toCSV(ingre_list_csv)

    
    
    

            
            