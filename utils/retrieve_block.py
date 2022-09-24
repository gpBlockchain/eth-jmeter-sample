# !/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
 @Time    : 2022/9/14 14:39
 @Author  : fReBourne
 @Email   :
 @File    : retrieve_block.py
 @Project : untitled
 """
import csv
from web3 import Web3
import time
import sys

blockMsg={}

def get_block_number(gwa):
    origin_number = gwa.eth.block_number
    number = origin_number
    while 1:
        new_number = gwa.eth.block_number
        if new_number != number:
            number = new_number
            return number
        else:
            continue


def get_block(gwa, blocknumber):
    blk = gwa.eth.get_block(blocknumber)
    return blk


def parse_block(block):
    local_timep = time.time()
    local_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    block_number = block.number
    txns = [data.hex() for data in block.transactions]
    remote_tp = block.timestamp
    timeArray = time.localtime(remote_tp)
    remote_tm = time.strftime("%Y-%m-%d %H:%M:%S", timeArray)
    txn_count = len(txns)
    tps = 0
    if (block_number-1) in blockMsg.keys():
        tps = txn_count/(local_timep - blockMsg[block_number-1][1])
    blockMsg[block_number] = [local_time, local_timep, remote_tp, remote_tm, block_number, txn_count,tps]
    return [local_time, local_timep, remote_tp, remote_tm, block_number, txn_count,tps]


def write_data(filaname, data_list):
    """数据写入到csv文件,续写或新建

    :param filaname: string类型，csv文件路径
    :param data_list: list类型，写入数据
    :param option: string类型，保存方式，是新建(new)还是追加(append)
    :return: xx类型，返回值说明

    author:
    created: 12/21/2021 15:55
    update:

    """
    csv_name = '{}.csv'.format(filaname)
    with open(csv_name, 'a+', newline='') as file:
        csv_file = csv.writer(file)
        csv_file.writerow(data_list)
        file.close()


if __name__ == '__main__':
    httpUrl = 'https://godwoken-alphanet-v1.ckbapp.dev'
    if len(sys.argv)==2 and sys.argv[1] == "testnet":
        httpUrl = 'https://godwoken-testnet-v1.ckbapp.dev'
        print("use test net ")
    else:
        print("use default net")

    clt = Web3(Web3.HTTPProvider(httpUrl))

    while 1:
        # 获取number
        number = get_block_number(clt)
        # 获取block
        blk = get_block(clt, number)
        # 解析block数据
        blokdata = parse_block(blk)
        with open(r"./block.csv", 'a+', newline='') as file:
            csv_file = csv.writer(file)
            csv_file.writerow(blokdata)
            file.close()
