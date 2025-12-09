///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

type NameGetter<T> = ((arg0: T)=> string);

interface Named {
    name: string;
}

export class TreeNode<PayLoad> {
    public parent: TreeNode<PayLoad>;
    private _children: TreeNode<PayLoad>[];
    public name: string;

    constructor(
            public payload: PayLoad,
            children: TreeNode<PayLoad>[],
            /** 
             * Le paramètre name peut être fourni directement, 
             * ou comme chaîne de caractères, ou sous la forme d'une fonction
             * qui l'extrait du payload
             */
            name: string |  NameGetter<PayLoad> = (<Named>payload).name
        ){
        this.name = this.nameFromNameInput(name);
        if(children){
            this._children=children.slice();
            this._children.forEach(
                (child: TreeNode<PayLoad>) => child.parent = this
            )
        }
    }
    
    private nameFromNameInput(name: string|NameGetter<PayLoad>): string{
        if(typeof name === 'string'){
            return name;
        }else{
            return name(this.payload);
        }
    }

    public get children(): TreeNode<PayLoad>[]{
        const parent=this;
        const delegate=this._children;
        let wrapper;
        if(this._children){
        wrapper=this._children.slice();
        wrapper.push = (...items: TreeNode<PayLoad>[]) => {
                items.forEach(
                    (item: TreeNode<PayLoad>) => item.parent=parent
                );
                return delegate.push(...items);
        };
        }else{
            wrapper=null;
        }
        return wrapper;
    }

    hasChildren(): boolean{
        return this._children?.length>0;
    }
}
